package pe.MIKHUY.ServiceImplements;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.MIKHUY.DTOs.response.*;
import pe.MIKHUY.Entities.Estudiante;
import pe.MIKHUY.Repositories.AnalisisNutricionalRepository;
import pe.MIKHUY.Repositories.EstudianteRepository;
import pe.MIKHUY.Service.*;

import java.time.LocalDate;
import java.util.List;
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

        // 7. Último análisis nutricional (si existe)
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
                .build();
    }
}
