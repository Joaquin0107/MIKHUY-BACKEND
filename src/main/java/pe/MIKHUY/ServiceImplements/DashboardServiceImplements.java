package pe.MIKHUY.ServiceImplements;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.MIKHUY.DTOs.response.*;
import pe.MIKHUY.Entities.Estudiante;
import pe.MIKHUY.Entities.MedicionSalud;
import pe.MIKHUY.Repositories.*;
import pe.MIKHUY.Service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImplements implements DashboardService {

    private final EstudianteService estudianteService;
    private final JuegoService juegoService;
    private final SesionJuegoService sesionJuegoService;
    private final NotificacionService notificacionService;
    private final BeneficioService beneficioService;
    private final EstudianteRepository estudianteRepository;
    private final AnalisisNutricionalRepository analisisNutricionalRepository;
    private final MedicionSaludRepository medicionSaludRepository;

    @Override
    public DashboardEstudianteResponse getDashboardEstudiante(UUID estudianteId) {
        log.info("Obteniendo dashboard completo para estudiante: {}", estudianteId);

        // 1. Perfil del estudiante
        EstudianteResponse estudiante = estudianteService.getById(estudianteId);

        // 2. Estadísticas
        EstadisticasEstudianteResponse estadisticas = estudianteService.getEstadisticas(estudianteId);

        // 3. Juegos con progreso
        List<JuegoResponse> juegos = juegoService.getJuegosConProgreso(estudianteId);

        // 4. Últimas 5 sesiones
        List<SesionJuegoResponse> ultimasSesiones = sesionJuegoService.getSesionesByEstudiante(estudianteId)
                .stream()
                .limit(5)
                .collect(Collectors.toList());

        // 5. Últimas 5 notificaciones no leídas
        List<NotificacionResponse> notificaciones = notificacionService.getNotificacionesNoLeidas(
                        estudiante.getUsuarioId()
                ).stream()
                .limit(5)
                .collect(Collectors.toList());

        // 6. Beneficios disponibles (top 5)
        List<BeneficioResponse> beneficiosDisponibles = beneficioService.getDisponiblesParaEstudiante(estudianteId)
                .stream()
                .limit(5)
                .collect(Collectors.toList());

        // 7. Último análisis nutricional
        AnalisisNutricionalResponse ultimoAnalisis = null;
        try {
            var analisisOpt = analisisNutricionalRepository.findFirstByEstudianteIdOrderByFechaAnalisisDesc(estudianteId);
            if (analisisOpt.isPresent()) {
                var analisis = analisisOpt.get();
                ultimoAnalisis = AnalisisNutricionalResponse.builder()
                        .id(analisis.getId())
                        .estudianteId(analisis.getEstudiante().getId())
                        .fechaAnalisis(analisis.getFechaAnalisis())
                        .proteinasPorcentaje(analisis.getProteinasPorcentaje())
                        .carbohidratosPorcentaje(analisis.getCarbohidratosPorcentaje())
                        .grasasPorcentaje(analisis.getGrasasPorcentaje())
                        .etapaCambio(analisis.getEtapaCambio())
                        .porcentajeAciertos(analisis.getPorcentajeAciertos())
                        .build();
            }
        } catch (Exception e) {
            log.warn("No se pudo obtener análisis nutricional: {}", e.getMessage());
        }

        // 8. Ranking (top 5 + posición del estudiante)
        RankingResponse rankingCompleto = estudianteService.getRanking(estudianteId);
        List<RankingResponse.EstudianteRanking> top5 = rankingCompleto.getRanking().stream()
                .limit(5)
                .collect(Collectors.toList());

        DashboardEstudianteResponse.RankingInfo rankingInfo = DashboardEstudianteResponse.RankingInfo.builder()
                .posicion(rankingCompleto.getMiPosicion() != null ?
                        rankingCompleto.getMiPosicion().getPosicion() : 0)
                .total(rankingCompleto.getTotalEstudiantes())
                .top5(top5.stream()
                        .map(e -> DashboardEstudianteResponse.TopEstudiante.builder()
                                .nombre(e.getNombre())
                                .puntos(e.getPuntosAcumulados())
                                .posicion(e.getPosicion())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        // ✅ 9. INFORMACIÓN DE SALUD
        DashboardEstudianteResponse.SaludInfo saludInfo = obtenerInformacionSalud(estudianteId);

        // Construir dashboard completo
        return DashboardEstudianteResponse.builder()
                .estudiante(estudiante)
                .estadisticas(estadisticas)
                .juegos(juegos)
                .ultimasSesiones(ultimasSesiones)
                .notificaciones(notificaciones)
                .beneficiosDisponibles(beneficiosDisponibles)
                .ultimoAnalisis(ultimoAnalisis)
                .ranking(rankingInfo)
                .salud(saludInfo) // ✅ NUEVO
                .build();
    }

    /**
     * ✅ NUEVO: Obtener información consolidada de salud
     */
    private DashboardEstudianteResponse.SaludInfo obtenerInformacionSalud(UUID estudianteId) {
        try {
            // Obtener última medición
            Optional<MedicionSalud> ultimaMedicionOpt = medicionSaludRepository
                    .findFirstByEstudianteIdOrderByFechaRegistroDesc(estudianteId);

            MedicionSaludResponse medicionActual = null;
            if (ultimaMedicionOpt.isPresent()) {
                medicionActual = mapMedicionToResponse(ultimaMedicionOpt.get());
            }

            // Obtener historial (últimas 12 mediciones para gráfica de tendencia)
            List<MedicionSaludResponse> historial = medicionSaludRepository
                    .findByEstudianteIdOrderByFechaRegistroDesc(estudianteId)
                    .stream()
                    .limit(12)
                    .map(this::mapMedicionToResponse)
                    .collect(Collectors.toList());

            // Calcular estadísticas
            DashboardEstudianteResponse.EstadisticasSalud estadisticas =
                    calcularEstadisticasSalud(historial);

            return DashboardEstudianteResponse.SaludInfo.builder()
                    .medicionActual(medicionActual)
                    .historialMediciones(historial)
                    .estadisticas(estadisticas)
                    .build();

        } catch (Exception e) {
            log.error("Error obteniendo información de salud: {}", e.getMessage());
            return DashboardEstudianteResponse.SaludInfo.builder()
                    .medicionActual(null)
                    .historialMediciones(List.of())
                    .estadisticas(null)
                    .build();
        }
    }

    /**
     * Calcular estadísticas de salud
     */
    private DashboardEstudianteResponse.EstadisticasSalud calcularEstadisticasSalud(
            List<MedicionSaludResponse> historial) {

        if (historial.isEmpty()) {
            return DashboardEstudianteResponse.EstadisticasSalud.builder()
                    .imcActual(0.0)
                    .estadoNutricionalActual("Sin mediciones")
                    .variacionPeso(0.0)
                    .variacionTalla(0.0)
                    .totalMediciones(0)
                    .tendencia("Sin datos")
                    .recomendacion("Se recomienda realizar una medición de salud.")
                    .build();
        }

        MedicionSaludResponse actual = historial.get(0);

        // Calcular variaciones si hay medición anterior
        Double variacionPeso = 0.0;
        Double variacionTalla = 0.0;
        String tendencia = "Estable";

        if (historial.size() > 1) {
            MedicionSaludResponse anterior = historial.get(1);

            // Variación de peso (%)
            if (anterior.getPeso() != null && actual.getPeso() != null) {
                BigDecimal diff = actual.getPeso().subtract(anterior.getPeso());
                variacionPeso = diff.divide(anterior.getPeso(), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .doubleValue();
            }

            // Variación de talla (%)
            if (anterior.getTalla() != null && actual.getTalla() != null) {
                BigDecimal diff = actual.getTalla().subtract(anterior.getTalla());
                variacionTalla = diff.divide(anterior.getTalla(), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .doubleValue();
            }

            // Determinar tendencia
            tendencia = determinarTendencia(actual.getEstadoNutricional(),
                    anterior.getEstadoNutricional());
        }

        // Generar recomendación personalizada
        String recomendacion = generarRecomendacion(
                actual.getEstadoNutricional(),
                actual.getImc() != null ? actual.getImc().doubleValue() : 0.0,
                variacionPeso
        );

        return DashboardEstudianteResponse.EstadisticasSalud.builder()
                .imcActual(actual.getImc() != null ? actual.getImc().doubleValue() : 0.0)
                .estadoNutricionalActual(actual.getEstadoNutricional())
                .variacionPeso(variacionPeso)
                .variacionTalla(variacionTalla)
                .totalMediciones(historial.size())
                .tendencia(tendencia)
                .recomendacion(recomendacion)
                .build();
    }

    /**
     * Determinar tendencia de salud
     */
    private String determinarTendencia(String estadoActual, String estadoAnterior) {
        if (estadoActual.equals(estadoAnterior)) {
            return "Estable";
        }

        List<String> orden = List.of("Bajo peso", "Normal", "Sobrepeso", "Obesidad");
        int indexActual = orden.indexOf(estadoActual);
        int indexAnterior = orden.indexOf(estadoAnterior);

        if (indexActual < indexAnterior) {
            return "Mejorando"; // Se movió hacia "Normal"
        } else {
            return "Preocupante"; // Se alejó de "Normal"
        }
    }

    /**
     * Generar recomendación personalizada
     */
    private String generarRecomendacion(String estado, Double imc, Double variacionPeso) {
        switch (estado) {
            case "Bajo peso":
                return "Se recomienda una alimentación balanceada con mayor aporte calórico. " +
                        "Consultar con nutricionista para plan personalizado.";

            case "Normal":
                if (Math.abs(variacionPeso) < 2) {
                    return "¡Excelente! El estudiante mantiene un peso saludable. " +
                            "Continuar con hábitos alimenticios balanceados y actividad física regular.";
                } else {
                    return "Estado normal, pero monitorear cambios de peso. " +
                            "Mantener alimentación equilibrada y actividad física.";
                }

            case "Sobrepeso":
                return "Se recomienda incrementar actividad física y ajustar alimentación. " +
                        "Reducir consumo de alimentos procesados y bebidas azucaradas.";

            case "Obesidad":
                return "Es importante consultar con un especialista en nutrición. " +
                        "Se requiere plan alimenticio y de actividad física supervisado.";

            default:
                return "Realizar medición de peso y talla para evaluación completa.";
        }
    }

    /**
     * Mapper de MedicionSalud a MedicionSaludResponse
     */
    private MedicionSaludResponse mapMedicionToResponse(MedicionSalud medicion) {
        return MedicionSaludResponse.builder()
                .id(medicion.getId().toString())
                .estudianteId(medicion.getEstudiante().getId().toString())
                .peso(medicion.getPeso())
                .talla(medicion.getTalla())
                .imc(medicion.getImc())
                .estadoNutricional(medicion.getEstadoNutricional())
                .fechaRegistro(medicion.getFechaRegistro())
                .notas(medicion.getNotas())
                .build();
    }
}