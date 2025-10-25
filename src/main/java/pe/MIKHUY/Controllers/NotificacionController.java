package pe.MIKHUY.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.MIKHUY.DTOs.ApiResponse;
import pe.MIKHUY.DTOs.response.NotificacionResponse;
import pe.MIKHUY.Security.CurrentUserUtil;
import pe.MIKHUY.Service.NotificacionService;

import java.util.List;
import java.util.UUID;

/**
 * Controlador de Notificaciones
 * Endpoints: /api/notificaciones/**
 */
@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final CurrentUserUtil currentUserUtil;

    /**
     * Obtener mis notificaciones
     * GET /api/notificaciones
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<NotificacionResponse>>> getMisNotificaciones(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            List<NotificacionResponse> notificaciones = notificacionService.getNotificacionesByUsuario(usuarioId);

            return ResponseEntity.ok(
                    ApiResponse.success("Notificaciones obtenidas", notificaciones)
            );
        } catch (Exception e) {
            log.error("Error obteniendo notificaciones: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtener notificaciones no leídas
     * GET /api/notificaciones/no-leidas
     */
    @GetMapping("/no-leidas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<NotificacionResponse>>> getNoLeidas(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            List<NotificacionResponse> notificaciones = notificacionService.getNotificacionesNoLeidas(usuarioId);

            return ResponseEntity.ok(
                    ApiResponse.success("Notificaciones no leídas", notificaciones)
            );
        } catch (Exception e) {
            log.error("Error obteniendo notificaciones no leídas: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Contar notificaciones no leídas
     * GET /api/notificaciones/count-no-leidas
     */
    @GetMapping("/count-no-leidas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Integer>> contarNoLeidas(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            int count = notificacionService.contarNoLeidas(usuarioId);

            return ResponseEntity.ok(
                    ApiResponse.success("Total no leídas", count)
            );
        } catch (Exception e) {
            log.error("Error contando notificaciones: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Marcar notificación como leída
     * PUT /api/notificaciones/{id}/leer
     */
    @PutMapping("/{id}/leer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<NotificacionResponse>> marcarComoLeida(@PathVariable UUID id) {
        try {
            NotificacionResponse notificacion = notificacionService.marcarComoLeida(id);

            return ResponseEntity.ok(
                    ApiResponse.success("Notificación marcada como leída", notificacion)
            );
        } catch (Exception e) {
            log.error("Error marcando notificación: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Marcar todas como leídas
     * PUT /api/notificaciones/leer-todas
     */
    @PutMapping("/leer-todas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> marcarTodasComoLeidas(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            notificacionService.marcarTodasComoLeidas(usuarioId);

            return ResponseEntity.ok(
                    ApiResponse.success("Todas las notificaciones marcadas como leídas", null)
            );
        } catch (Exception e) {
            log.error("Error marcando todas las notificaciones: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Eliminar notificación
     * DELETE /api/notificaciones/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> eliminarNotificacion(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID id) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            notificacionService.eliminarNotificacion(id, usuarioId);

            return ResponseEntity.ok(
                    ApiResponse.success("Notificación eliminada", null)
            );
        } catch (RuntimeException e) {
            log.error("Error eliminando notificación: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }
}