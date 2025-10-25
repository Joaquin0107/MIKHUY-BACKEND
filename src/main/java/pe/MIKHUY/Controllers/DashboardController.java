package pe.MIKHUY.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.MIKHUY.DTOs.ApiResponse;
import pe.MIKHUY.DTOs.response.DashboardEstudianteResponse;
import pe.MIKHUY.DTOs.response.EstudianteResponse;
import pe.MIKHUY.Security.CurrentUserUtil;
import pe.MIKHUY.Service.DashboardService;
import pe.MIKHUY.Service.EstudianteService;

import java.util.UUID;

/**
 * Controlador de Dashboard
 * Endpoints: /api/dashboard/**
 */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    private final DashboardService dashboardService;
    private final EstudianteService estudianteService;
    private final CurrentUserUtil currentUserUtil;

    /**
     * Obtener mi dashboard completo
     * GET /api/dashboard
     *
     * Retorna toda la información necesaria para la pantalla principal:
     * - Perfil del estudiante
     * - Estadísticas generales
     * - Juegos con progreso
     * - Últimas sesiones
     * - Notificaciones no leídas
     * - Beneficios disponibles
     * - Último análisis nutricional
     * - Ranking (top 5 + mi posición)
     */
    @GetMapping
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<DashboardEstudianteResponse>> getMiDashboard(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse estudiante = estudianteService.getPerfilByUsuarioId(usuarioId);

            DashboardEstudianteResponse dashboard = dashboardService.getDashboardEstudiante(estudiante.getId());

            return ResponseEntity.ok(
                    ApiResponse.success("Dashboard obtenido", dashboard)
            );
        } catch (Exception e) {
            log.error("Error obteniendo dashboard: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtener dashboard de un estudiante específico (para profesores/admin)
     * GET /api/dashboard/estudiante/{id}
     */
    @GetMapping("/estudiante/{id}")
    @PreAuthorize("hasAnyAuthority('teacher', 'admin')")
    public ResponseEntity<ApiResponse<DashboardEstudianteResponse>> getDashboardEstudiante(
            @PathVariable UUID id) {
        try {
            DashboardEstudianteResponse dashboard = dashboardService.getDashboardEstudiante(id);

            return ResponseEntity.ok(
                    ApiResponse.success("Dashboard obtenido", dashboard)
            );
        } catch (Exception e) {
            log.error("Error obteniendo dashboard: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }
}
