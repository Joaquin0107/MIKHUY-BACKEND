package pe.MIKHUY.ServiceImplements;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.MIKHUY.DTOs.request.*;
import pe.MIKHUY.DTOs.response.SesionJuegoResponse;
import pe.MIKHUY.Entities.*;
import pe.MIKHUY.Repositories.*;
import pe.MIKHUY.Service.SesionJuegoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SesionJuegoServiceImplements implements SesionJuegoService {

    private final SesionJuegoRepository sesionJuegoRepository;
    private final ProgresoJuegoRepository progresoJuegoRepository;
    private final EstudianteRepository estudianteRepository;
    private final JuegoRepository juegoRepository;
    private final NutrimentalRespuestaRepository nutrimentalRespuestaRepository;
    private final Reto7DiasRegistroRepository reto7DiasRegistroRepository;
    private final CoachRespuestaRepository coachRespuestaRepository;

    @Override
    @Transactional
    public SesionJuegoResponse iniciarSesion(UUID estudianteId, IniciarSesionJuegoRequest request) {
        log.info("Iniciando sesión de juego para estudiante: {}", estudianteId);

        // Verificar que el estudiante existe
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // Verificar que el juego existe
        Juego juego = juegoRepository.findById(request.getJuegoId())
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));

        // Buscar o crear progreso del estudiante en este juego
        ProgresoJuego progreso = progresoJuegoRepository
                .findByEstudianteIdAndJuegoId(estudianteId, request.getJuegoId())
                .orElseGet(() -> {
                    ProgresoJuego nuevoProgreso = new ProgresoJuego();
                    nuevoProgreso.setEstudiante(estudiante);
                    nuevoProgreso.setJuego(juego);
                    nuevoProgreso.setNivelActual(1);
                    nuevoProgreso.setPuntosGanados(0);
                    nuevoProgreso.setVecesJugado(0);
                    nuevoProgreso.setCompletado(false);
                    nuevoProgreso.setFechaInicio(LocalDateTime.now());
                    return progresoJuegoRepository.save(nuevoProgreso);
                });

        // Crear nueva sesión
        SesionJuego sesion = new SesionJuego();
        sesion.setProgreso(progreso);
        sesion.setNivelJugado(request.getNivel());
        sesion.setPuntosObtenidos(0);
        sesion.setTiempoJugado(0);
        sesion.setCompletado(false);
        sesion.setFechaSesion(LocalDateTime.now());

        sesion = sesionJuegoRepository.save(sesion);

        // Incrementar veces jugado
        progreso.incrementarVecesJugado();
        progresoJuegoRepository.save(progreso);

        log.info("Sesión iniciada con ID: {}", sesion.getId());

        return mapToResponse(sesion);
    }

    @Override
    @Transactional
    public SesionJuegoResponse finalizarSesion(FinalizarSesionJuegoRequest request) {
        log.info("Finalizando sesión: {}", request.getSesionId());

        // Buscar sesión
        SesionJuego sesion = sesionJuegoRepository.findById(request.getSesionId())
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        // Actualizar datos de la sesión
        sesion.setPuntosObtenidos(request.getPuntosObtenidos());
        sesion.setTiempoJugado(request.getTiempoJugado());
        sesion.setCompletado(request.getCompletado());

        sesion = sesionJuegoRepository.save(sesion);

        // Actualizar progreso
        ProgresoJuego progreso = sesion.getProgreso();
        progreso.agregarPuntos(request.getPuntosObtenidos());

        // Si completó el nivel, avanzar al siguiente
        if (request.getCompletado()) {
            if (progreso.getNivelActual() < progreso.getJuego().getMaxNiveles()) {
                progreso.incrementarNivel();
            } else {
                progreso.setCompletado(true);
            }
        }

        progresoJuegoRepository.save(progreso);

        // Actualizar puntos del estudiante
        Estudiante estudiante = progreso.getEstudiante();
        estudiante.agregarPuntos(request.getPuntosObtenidos());
        estudianteRepository.save(estudiante);

        log.info("Sesión finalizada. Puntos otorgados: {}", request.getPuntosObtenidos());

        return mapToResponse(sesion);
    }

    @Override
    @Transactional
    public void guardarRespuestaNutrimental(GuardarNutrimentalRespuestaRequest request) {
        log.info("Guardando respuesta Nutrimental para sesión: {}", request.getSesionId());

        SesionJuego sesion = sesionJuegoRepository.findById(request.getSesionId())
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        NutrimentalRespuesta respuesta = new NutrimentalRespuesta();
        respuesta.setSesion(sesion);
        respuesta.setPreguntaNumero(request.getPreguntaNumero());
        respuesta.setPreguntaTema(request.getPreguntaTema());
        respuesta.setRespuestaCorrecta(request.getRespuestaCorrecta());
        respuesta.setTiempoRespuesta(request.getTiempoRespuesta());
        respuesta.setFechaRespuesta(LocalDateTime.now());

        nutrimentalRespuestaRepository.save(respuesta);

        log.info("Respuesta Nutrimental guardada");
    }

    @Override
    @Transactional
    public void guardarRegistroReto7Dias(GuardarReto7DiasRegistroRequest request) {
        log.info("Guardando registro Reto 7 Días para sesión: {}", request.getSesionId());

        SesionJuego sesion = sesionJuegoRepository.findById(request.getSesionId())
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        Reto7DiasRegistro registro = new Reto7DiasRegistro();
        registro.setSesion(sesion);
        registro.setDiaNumero(request.getDiaNumero());
        registro.setMomentoDia(Reto7DiasRegistro.MomentoDiaEnum.valueOf(request.getMomentoDia()));
        registro.setAlimentosFrutas(request.getAlimentosFrutas());
        registro.setAlimentosVerduras(request.getAlimentosVerduras());
        registro.setAlimentosProteinas(request.getAlimentosProteinas());
        registro.setAlimentosCarbohidratos(request.getAlimentosCarbohidratos());
        registro.setAlimentosLacteos(request.getAlimentosLacteos());
        registro.setAlimentosDulces(request.getAlimentosDulces());

        if (request.getEmocion() != null) {
            registro.setEmocion(Reto7DiasRegistro.EmocionEnum.valueOf(request.getEmocion()));
        }

        registro.setCaloriasEstimadas(request.getCaloriasEstimadas());
        registro.setNotas(request.getNotas());
        registro.setFechaRegistro(LocalDateTime.now());

        reto7DiasRegistroRepository.save(registro);

        log.info("Registro Reto 7 Días guardado");
    }

    @Override
    @Transactional
    public void guardarRespuestaCoach(GuardarCoachRespuestaRequest request) {
        log.info("Guardando respuesta Coach para sesión: {}", request.getSesionId());

        SesionJuego sesion = sesionJuegoRepository.findById(request.getSesionId())
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        CoachRespuesta respuesta = new CoachRespuesta();
        respuesta.setSesion(sesion);
        respuesta.setPreguntaNumero(request.getPreguntaNumero());
        respuesta.setPreguntaEtapa(request.getPreguntaEtapa());
        respuesta.setRespuestaValor(request.getRespuestaValor());
        respuesta.setFechaRespuesta(LocalDateTime.now());

        coachRespuestaRepository.save(respuesta);

        log.info("Respuesta Coach guardada");
    }

    @Override
    public List<SesionJuegoResponse> getSesionesByEstudiante(UUID estudianteId) {
        log.info("Obteniendo sesiones del estudiante: {}", estudianteId);

        return sesionJuegoRepository.findByEstudianteId(estudianteId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SesionJuegoResponse getUltimaSesion(UUID progresoId) {
        log.info("Obteniendo última sesión del progreso: {}", progresoId);

        List<SesionJuego> sesiones = sesionJuegoRepository.findLastByProgresoId(progresoId);

        if (sesiones.isEmpty()) {
            throw new RuntimeException("No hay sesiones para este progreso");
        }

        return mapToResponse(sesiones.get(0));
    }

    /**
     * Mapear entidad a DTO Response
     */
    private SesionJuegoResponse mapToResponse(SesionJuego sesion) {
        String tiempoFormato = formatearTiempo(sesion.getTiempoJugado());

        return SesionJuegoResponse.builder()
                .id(sesion.getId())
                .progresoId(sesion.getProgreso().getId())
                .juegoNombre(sesion.getProgreso().getJuego().getNombre())
                .juegoCategoria(sesion.getProgreso().getJuego().getCategoria())
                .nivelJugado(sesion.getNivelJugado())
                .puntosObtenidos(sesion.getPuntosObtenidos())
                .tiempoJugado(sesion.getTiempoJugado())
                .tiempoJugadoFormato(tiempoFormato)
                .completado(sesion.getCompletado())
                .fechaSesion(sesion.getFechaSesion())
                .build();
    }

    /**
     * Formatear tiempo en segundos a HH:MM:SS
     */
    private String formatearTiempo(Integer segundos) {
        if (segundos == null || segundos == 0) {
            return "00:00:00";
        }

        int horas = segundos / 3600;
        int minutos = (segundos % 3600) / 60;
        int segs = segundos % 60;

        return String.format("%02d:%02d:%02d", horas, minutos, segs);
    }
}