package pe.MIKHUY.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.MIKHUY.DTOs.ApiResponse;
import pe.MIKHUY.DTOs.request.CanjeRequest;
import pe.MIKHUY.DTOs.response.CanjeResponse;
import pe.MIKHUY.DTOs.response.EstudianteResponse;
import pe.MIKHUY.Security.CurrentUserUtil;
import pe.MIKHUY.Service.CanjeService;
import pe.MIKHUY.Service.EstudianteService;

import java.util.List;
import java.util.UUID;

/**
 * Controlador de Canjes
 * Endpoints: /api/canjes/**
 */
@RestController
@RequestMapping("/canjes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class CanjeController {

    private final CanjeService canjeService;
    private final EstudianteService estudianteService;
    private final CurrentUserUtil currentUserUtil;

    /**
     * Realizar un canje
     * POST /api/canjes
     */
    @PostMapping
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<CanjeResponse>> realizarCanje(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CanjeRequest request) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse estudiante = estudianteService.getPerfilByUsuarioId(usuarioId);

            CanjeResponse canje = canjeService.realizarCanje(estudiante.getId(), request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Canje realizado exitosamente", canje));
        } catch (RuntimeException e) {
            log.error("Error realizando canje: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado en canje: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error en el servidor"));
        }
    }

    /**
     * Obtener mis canjes
     * GET /api/canjes/mis-canjes
     */
    @GetMapping("/mis-canjes")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<List<CanjeResponse>>> getMisCanjes(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse estudiante = estudianteService.getPerfilByUsuarioId(usuarioId);

            List<CanjeResponse> canjes = canjeService.getCanjesByEstudiante(estudiante.getId());

            return ResponseEntity.ok(
                    ApiResponse.success("Canjes obtenidos", canjes)
            );
        } catch (Exception e) {
            log.error("Error obteniendo canjes: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtener mis canjes pendientes
     * GET /api/canjes/pendientes
     */
    @GetMapping("/pendientes")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<List<CanjeResponse>>> getMisCanjesPendientes(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse estudiante = estudianteService.getPerfilByUsuarioId(usuarioId);

            List<CanjeResponse> canjes = canjeService.getCanjesPendientes(estudiante.getId());

            return ResponseEntity.ok(
                    ApiResponse.success("Canjes pendientes obtenidos", canjes)
            );
        } catch (Exception e) {
            log.error("Error obteniendo canjes pendientes: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Marcar canje como entregado (admin/profesor)
     * PUT /api/canjes/{id}/entregar
     */
    @PutMapping("/{id}/entregar")
    @PreAuthorize("hasAnyAuthority('teacher', 'admin')")
    public ResponseEntity<ApiResponse<CanjeResponse>> marcarComoEntregado(@PathVariable UUID id) {
        try {
            CanjeResponse canje = canjeService.marcarComoEntregado(id);

            return ResponseEntity.ok(
                    ApiResponse.success("Canje marcado como entregado", canje)
            );
        } catch (RuntimeException e) {
            log.error("Error marcando canje como entregado: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error en el servidor"));
        }
    }

    /**
     * Cancelar canje (solo si está pendiente)
     * DELETE /api/canjes/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<CanjeResponse>> cancelarCanje(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID id) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse estudiante = estudianteService.getPerfilByUsuarioId(usuarioId);

            CanjeResponse canje = canjeService.cancelarCanje(id, estudiante.getId());

            return ResponseEntity.ok(
                    ApiResponse.success("Canje cancelado. Puntos devueltos", canje)
            );
        } catch (RuntimeException e) {
            log.error("Error cancelando canje: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error en el servidor"));
        }
    }
}