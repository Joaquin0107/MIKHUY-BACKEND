package pe.MIKHUY.Service;

import pe.MIKHUY.DTOs.response.NotificacionResponse;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de Notificaciones
 */
public interface NotificacionService {

    /**
     * Crear notificación
     */
    NotificacionResponse crearNotificacion(UUID usuarioId, String tipo, String titulo, String mensaje);

    /**
     * Obtener notificaciones de un usuario
     */
    List<NotificacionResponse> getNotificacionesByUsuario(UUID usuarioId);

    /**
     * Obtener notificaciones no leídas
     */
    List<NotificacionResponse> getNotificacionesNoLeidas(UUID usuarioId);

    /**
     * Contar notificaciones no leídas
     */
    int contarNoLeidas(UUID usuarioId);

    /**
     * Marcar notificación como leída
     */
    NotificacionResponse marcarComoLeida(UUID notificacionId);

    /**
     * Marcar todas como leídas
     */
    void marcarTodasComoLeidas(UUID usuarioId);

    /**
     * Eliminar notificación
     */
    void eliminarNotificacion(UUID notificacionId, UUID usuarioId);

    /**
     * Eliminar notificaciones antiguas (más de 30 días)
     */
    void eliminarNotificacionesAntiguas();
}