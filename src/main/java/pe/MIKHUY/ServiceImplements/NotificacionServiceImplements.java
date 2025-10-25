package pe.MIKHUY.ServiceImplements;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.MIKHUY.DTOs.response.NotificacionResponse;
import pe.MIKHUY.Entities.Notificacion;
import pe.MIKHUY.Entities.Usuario;
import pe.MIKHUY.Repositories.NotificacionRepository;
import pe.MIKHUY.Repositories.UsuarioRepository;
import pe.MIKHUY.Service.NotificacionService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionServiceImplements implements NotificacionService{

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public NotificacionResponse crearNotificacion(UUID usuarioId, String tipo, String titulo, String mensaje) {
        log.info("Creando notificación para usuario: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setTipo(tipo);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setLeida(false);
        notificacion.setFechaCreacion(LocalDateTime.now());

        notificacion = notificacionRepository.save(notificacion);

        log.info("Notificación creada con ID: {}", notificacion.getId());

        return mapToResponse(notificacion);
    }

    @Override
    public List<NotificacionResponse> getNotificacionesByUsuario(UUID usuarioId) {
        log.info("Obteniendo notificaciones del usuario: {}", usuarioId);

        return notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificacionResponse> getNotificacionesNoLeidas(UUID usuarioId) {
        log.info("Obteniendo notificaciones no leídas del usuario: {}", usuarioId);

        return notificacionRepository.findByUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(usuarioId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public int contarNoLeidas(UUID usuarioId) {
        return (int) notificacionRepository.countByUsuarioIdAndLeidaFalse(usuarioId);
    }

    @Override
    @Transactional
    public NotificacionResponse marcarComoLeida(UUID notificacionId) {
        log.info("Marcando notificación como leída: {}", notificacionId);

        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        notificacion.setLeida(true);
        notificacion = notificacionRepository.save(notificacion);

        return mapToResponse(notificacion);
    }

    @Override
    @Transactional
    public void marcarTodasComoLeidas(UUID usuarioId) {
        log.info("Marcando todas las notificaciones como leídas para usuario: {}", usuarioId);
        notificacionRepository.marcarTodasComoLeidas(usuarioId);
    }

    @Override
    @Transactional
    public void eliminarNotificacion(UUID notificacionId, UUID usuarioId) {
        log.info("Eliminando notificación: {}", notificacionId);

        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        // Verificar que la notificación pertenece al usuario
        if (!notificacion.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permisos para eliminar esta notificación");
        }

        notificacionRepository.delete(notificacion);
        log.info("Notificación eliminada");
    }

    @Override
    @Transactional
    public void eliminarNotificacionesAntiguas() {
        log.info("Eliminando notificaciones antiguas (más de 30 días)");
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);
        int eliminadas = notificacionRepository.deleteOlderThan(hace30Dias);
        log.info("Notificaciones eliminadas: {}", eliminadas);
    }

    /**
     * Mapear entidad a DTO Response
     */
    private NotificacionResponse mapToResponse(Notificacion notificacion) {
        String tiempoTranscurrido = calcularTiempoTranscurrido(notificacion.getFechaCreacion());

        return NotificacionResponse.builder()
                .id(notificacion.getId())
                .tipo(notificacion.getTipo())
                .titulo(notificacion.getTitulo())
                .mensaje(notificacion.getMensaje())
                .leida(notificacion.getLeida())
                .fechaCreacion(notificacion.getFechaCreacion())
                .tiempoTranscurrido(tiempoTranscurrido)
                .build();
    }

    /**
     * Calcular tiempo transcurrido desde una fecha
     */
    private String calcularTiempoTranscurrido(LocalDateTime fecha) {
        Duration duracion = Duration.between(fecha, LocalDateTime.now());
        long segundos = duracion.getSeconds();

        if (segundos < 60) {
            return "Hace " + segundos + " segundos";
        } else if (segundos < 3600) {
            long minutos = segundos / 60;
            return "Hace " + minutos + (minutos == 1 ? " minuto" : " minutos");
        } else if (segundos < 86400) {
            long horas = segundos / 3600;
            return "Hace " + horas + (horas == 1 ? " hora" : " horas");
        } else {
            long dias = segundos / 86400;
            return "Hace " + dias + (dias == 1 ? " día" : " días");
        }
    }
}