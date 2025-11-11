package pe.MIKHUY.ServiceImplements;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.MIKHUY.DTOs.request.UpdateProfileRequest;
import pe.MIKHUY.DTOs.response.EstadisticasEstudianteResponse;
import pe.MIKHUY.DTOs.response.EstudianteResponse;
import pe.MIKHUY.DTOs.response.RankingResponse;
import pe.MIKHUY.Entities.Estudiante;
import pe.MIKHUY.Entities.Usuario;
import pe.MIKHUY.Repositories.*;
import pe.MIKHUY.Service.EstudianteService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EstudianteServiceImplements implements EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProgresoJuegoRepository progresoJuegoRepository;
    private final SesionJuegoRepository sesionJuegoRepository;
    private final CanjeRepository canjeRepository;
    private final NotificacionRepository notificacionRepository;

    @Override
    public EstudianteResponse getPerfilByUsuarioId(UUID usuarioId) {
        log.info("Obteniendo perfil del estudiante por usuario ID: {}", usuarioId);

        Estudiante estudiante = estudianteRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        return mapToResponse(estudiante);
    }

    @Override
    public Integer getPuntos(UUID estudianteId) {
        log.info("Obteniendo puntos del estudiante: {}", estudianteId);

        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        return estudiante.getPuntosAcumulados() != null ? estudiante.getPuntosAcumulados() : 0;
    }

    @Override
    public EstudianteResponse getById(UUID id) {
        log.info("Obteniendo estudiante por ID: {}", id);

        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        return mapToResponse(estudiante);
    }

    @Override
    public List<EstudianteResponse> getAll() {
        log.info("Obteniendo todos los estudiantes");

        return estudianteRepository.findAllWithActiveUser().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EstudianteResponse> getByGrado(String grado) {
        log.info("Obteniendo estudiantes del grado: {}", grado);

        return estudianteRepository.findActiveByGrado(grado).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EstudianteResponse> getByGradoAndSeccion(String grado, String seccion) {
        log.info("Obteniendo estudiantes del grado: {} sección: {}", grado, seccion);

        return estudianteRepository.findByGradoAndSeccion(grado, seccion).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EstudianteResponse updatePerfil(UUID usuarioId, UpdateProfileRequest request) {
        log.info("Actualizando perfil del estudiante usuario ID: {}", usuarioId);

        // Buscar usuario y estudiante
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Estudiante estudiante = estudianteRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // Actualizar datos del usuario
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setTelefono(request.getTelefono());
        if (request.getAvatarUrl() != null) {
            usuario.setAvatarUrl(request.getAvatarUrl());
        }
        usuarioRepository.save(usuario);

        // Actualizar datos del estudiante
        if (request.getEdad() != null) {
            estudiante.setEdad(request.getEdad());
        }
        if (request.getGrado() != null) {
            estudiante.setGrado(request.getGrado());
        }
        if (request.getSeccion() != null) {
            estudiante.setSeccion(request.getSeccion());
        }
        if (request.getTalla() != null) {
            estudiante.setTalla(request.getTalla());
        }
        if (request.getPeso() != null) {
            estudiante.setPeso(request.getPeso());
        }
        estudianteRepository.save(estudiante);

        log.info("Perfil actualizado exitosamente para estudiante: {}", estudiante.getId());

        return mapToResponse(estudiante);
    }

    @Override
    public EstadisticasEstudianteResponse getEstadisticas(UUID estudianteId) {
        log.info("Obteniendo estadísticas del estudiante: {}", estudianteId);

        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // Calcular estadísticas
        Integer puntosGanados = progresoJuegoRepository.getTotalPuntosGanadosByEstudiante(estudianteId);
        Integer puntosGastados = canjeRepository.getTotalPuntosGastadosByEstudiante(estudianteId);
        long juegosJugados = progresoJuegoRepository.countByEstudianteIdAndCompletadoTrue(estudianteId);
        long juegosCompletados = progresoJuegoRepository.countByEstudianteIdAndCompletadoTrue(estudianteId);
        long totalSesiones = sesionJuegoRepository.countByEstudianteId(estudianteId);
        Integer tiempoTotalJugado = sesionJuegoRepository.getTiempoTotalByEstudiante(estudianteId);

        // Ranking
        List<Estudiante> todosEstudiantes = estudianteRepository.findTopByPuntosAcumuladosOrderByDesc();
        int posicionRanking = 0;
        for (int i = 0; i < todosEstudiantes.size(); i++) {
            if (todosEstudiantes.get(i).getId().equals(estudianteId)) {
                posicionRanking = i + 1;
                break;
            }
        }

        // Notificaciones no leídas
        long notificacionesNoLeidas = notificacionRepository.countByUsuarioIdAndLeidaFalse(
                estudiante.getUsuario().getId()
        );

        return EstadisticasEstudianteResponse.builder()
                .puntosAcumulados(estudiante.getPuntosAcumulados())
                .puntosGanados(puntosGanados != null ? puntosGanados : 0)
                .puntosGastados(puntosGastados != null ? puntosGastados : 0)
                .juegosJugados((int) juegosJugados)
                .juegosCompletados((int) juegosCompletados)
                .totalSesiones((int) totalSesiones)
                .tiempoTotalJugado(tiempoTotalJugado != null ? tiempoTotalJugado : 0)
                .posicionRanking(posicionRanking)
                .totalEstudiantes(todosEstudiantes.size())
                .notificacionesNoLeidas((int) notificacionesNoLeidas)
                .build();
    }

    @Override
    public RankingResponse getRanking(UUID estudianteIdActual) {
        log.info("Obteniendo ranking de estudiantes");

        List<Estudiante> estudiantesOrdenados = estudianteRepository.findTopByPuntosAcumuladosOrderByDesc();

        List<RankingResponse.EstudianteRanking> ranking = new ArrayList<>();
        RankingResponse.EstudianteRanking miPosicion = null;

        for (int i = 0; i < estudiantesOrdenados.size(); i++) {
            Estudiante est = estudiantesOrdenados.get(i);
            long juegosCompletados = progresoJuegoRepository.countByEstudianteIdAndCompletadoTrue(est.getId());

            RankingResponse.EstudianteRanking item = RankingResponse.EstudianteRanking.builder()
                    .posicion(i + 1)
                    .estudianteId(est.getId())
                    .nombre(est.getUsuario().getNombreCompleto())
                    .grado(est.getGrado())
                    .seccion(est.getSeccion())
                    .puntosAcumulados(est.getPuntosAcumulados())
                    .avatarUrl(est.getUsuario().getAvatarUrl())
                    .esTop3(i < 3)
                    .esMiPosicion(est.getId().equals(estudianteIdActual))
                    .juegosCompletados((int) juegosCompletados)
                    .build();

            ranking.add(item);

            if (est.getId().equals(estudianteIdActual)) {
                miPosicion = item;
            }
        }

        return RankingResponse.builder()
                .ranking(ranking)
                .totalEstudiantes(ranking.size())
                .miPosicion(miPosicion)
                .build();
    }

    @Override
    @Transactional
    public void agregarPuntos(UUID estudianteId, Integer puntos) {
        log.info("Agregando {} puntos al estudiante: {}", puntos, estudianteId);

        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        estudiante.agregarPuntos(puntos);
        estudianteRepository.save(estudiante);

        log.info("Puntos agregados. Total actual: {}", estudiante.getPuntosAcumulados());
    }

    @Override
    @Transactional
    public boolean descontarPuntos(UUID estudianteId, Integer puntos) {
        log.info("Descontando {} puntos del estudiante: {}", puntos, estudianteId);

        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        boolean exito = estudiante.descontarPuntos(puntos);

        if (exito) {
            estudianteRepository.save(estudiante);
            log.info("Puntos descontados. Total actual: {}", estudiante.getPuntosAcumulados());
        } else {
            log.warn("No se pudieron descontar puntos. Puntos insuficientes");
        }

        return exito;
    }

    /**
     * Mapear entidad a DTO Response
     */
    private EstudianteResponse mapToResponse(Estudiante estudiante) {
        Usuario usuario = estudiante.getUsuario();

        // Calcular estadísticas básicas
        long juegosJugados = progresoJuegoRepository.countByEstudianteIdAndCompletadoTrue(estudiante.getId());
        long juegosCompletados = progresoJuegoRepository.countByEstudianteIdAndCompletadoTrue(estudiante.getId());
        long totalSesiones = sesionJuegoRepository.countByEstudianteId(estudiante.getId());

        return EstudianteResponse.builder()
                .id(estudiante.getId())
                .usuarioId(usuario.getId())
                .email(usuario.getEmail())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .nombreCompleto(usuario.getNombreCompleto())
                .telefono(usuario.getTelefono())
                .avatarUrl(usuario.getAvatarUrl())
                .edad(estudiante.getEdad())
                .grado(estudiante.getGrado())
                .seccion(estudiante.getSeccion())
                .talla(estudiante.getTalla())
                .peso(estudiante.getPeso())
                .imc(estudiante.calcularIMC())
                .puntosAcumulados(estudiante.getPuntosAcumulados())
                .juegosJugados((int) juegosJugados)
                .juegosCompletados((int) juegosCompletados)
                .totalSesiones((int) totalSesiones)
                .fechaRegistro(estudiante.getFechaRegistro())
                .ultimaConexion(usuario.getUltimaConexion())
                .build();
    }
}