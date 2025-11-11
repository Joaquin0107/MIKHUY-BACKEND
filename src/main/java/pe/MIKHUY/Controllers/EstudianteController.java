package pe.MIKHUY.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.MIKHUY.DTOs.ApiResponse;
import pe.MIKHUY.DTOs.request.UpdateProfileRequest;
import pe.MIKHUY.DTOs.response.EstadisticasEstudianteResponse;
import pe.MIKHUY.DTOs.response.EstudianteResponse;
import pe.MIKHUY.DTOs.response.RankingResponse;
import pe.MIKHUY.Security.CurrentUserUtil;
import pe.MIKHUY.Service.EstudianteService;

import java.util.List;
import java.util.UUID;

/**
 * Controlador de Estudiantes
 * Endpoints: /api/estudiantes/**
 */
@RestController
@RequestMapping("/estudiantes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class EstudianteController {
    private final EstudianteService estudianteService;
    private final CurrentUserUtil currentUserUtil;

    /**
     * Obtener mi perfil (estudiante autenticado)
     * GET /api/estudiantes/perfil
     */
    @GetMapping("/perfil")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<EstudianteResponse>> getMiPerfil(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse estudiante = estudianteService.getPerfilByUsuarioId(usuarioId);

            return ResponseEntity.ok(
                    ApiResponse.success("Perfil obtenido", estudiante)
            );
        } catch (Exception e) {
            log.error("Error obteniendo perfil: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error obteniendo perfil: " + e.getMessage()));
        }
    }

    /**
     * Actualizar mi perfil
     * PUT /api/estudiantes/perfil
     */
    @PutMapping("/perfil")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<EstudianteResponse>> updateMiPerfil(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UpdateProfileRequest request) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse estudiante = estudianteService.updatePerfil(usuarioId, request);

            return ResponseEntity.ok(
                    ApiResponse.success("Perfil actualizado", estudiante)
            );
        } catch (Exception e) {
            log.error("Error actualizando perfil: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error actualizando perfil: " + e.getMessage()));
        }
    }

    /**
     * Obtener mis puntos acumulados
     * GET /api/estudiantes/puntos
     */
    @GetMapping("/puntos")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<Integer>> getMisPuntos(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse estudiante = estudianteService.getPerfilByUsuarioId(usuarioId);
            Integer puntos = estudiante.getPuntosAcumulados() != null ?
                    estudiante.getPuntosAcumulados() : 0;

            return ResponseEntity.ok(
                    ApiResponse.success("Puntos obtenidos correctamente", puntos)
            );
        } catch (Exception e) {
            log.error("Error obteniendo puntos: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error obteniendo puntos: " + e.getMessage()));
        }
    }

    /**
     * Obtener mis estadísticas
     * GET /api/estudiantes/estadisticas
     */
    @GetMapping("/estadisticas")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<EstadisticasEstudianteResponse>> getMisEstadisticas(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse estudiante = estudianteService.getPerfilByUsuarioId(usuarioId);
            EstadisticasEstudianteResponse estadisticas = estudianteService.getEstadisticas(estudiante.getId());

            return ResponseEntity.ok(
                    ApiResponse.success("Estadísticas obtenidas", estadisticas)
            );
        } catch (Exception e) {
            log.error("Error obteniendo estadísticas: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error obteniendo estadísticas: " + e.getMessage()));
        }
    }

    /**
     * Obtener ranking global
     * GET /api/estudiantes/ranking
     */
    @GetMapping("/ranking")
    @PreAuthorize("hasAnyAuthority('student', 'teacher', 'admin')")
    public ResponseEntity<ApiResponse<RankingResponse>> getRanking(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            UUID estudianteId = null;

            // Si hay usuario autenticado y es estudiante, obtener su ID
            if (authHeader != null && currentUserUtil.isStudent()) {
                UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
                EstudianteResponse estudiante = estudianteService.getPerfilByUsuarioId(usuarioId);
                estudianteId = estudiante.getId();
            }

            RankingResponse ranking = estudianteService.getRanking(estudianteId);

            return ResponseEntity.ok(
                    ApiResponse.success("Ranking obtenido", ranking)
            );
        } catch (Exception e) {
            log.error("Error obteniendo ranking: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error obteniendo ranking: " + e.getMessage()));
        }
    }

    /**
     * Obtener estudiante por ID (admin/profesor)
     * GET /api/estudiantes/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('teacher', 'admin')")
    public ResponseEntity<ApiResponse<EstudianteResponse>> getById(@PathVariable UUID id) {
        try {
            EstudianteResponse estudiante = estudianteService.getById(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Estudiante encontrado", estudiante)
            );
        } catch (Exception e) {
            log.error("Error obteniendo estudiante: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Listar todos los estudiantes (admin/profesor)
     * GET /api/estudiantes
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('teacher', 'admin')")
    public ResponseEntity<ApiResponse<List<EstudianteResponse>>> getAll() {
        try {
            List<EstudianteResponse> estudiantes = estudianteService.getAll();
            return ResponseEntity.ok(
                    ApiResponse.success("Lista de estudiantes", estudiantes)
            );
        } catch (Exception e) {
            log.error("Error listando estudiantes: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Filtrar por grado (admin/profesor)
     * GET /api/estudiantes/grado/{grado}
     */
    @GetMapping("/grado/{grado}")
    @PreAuthorize("hasAnyAuthority('teacher', 'admin')")
    public ResponseEntity<ApiResponse<List<EstudianteResponse>>> getByGrado(@PathVariable String grado) {
        try {
            List<EstudianteResponse> estudiantes = estudianteService.getByGrado(grado);
            return ResponseEntity.ok(
                    ApiResponse.success("Estudiantes del grado " + grado, estudiantes)
            );
        } catch (Exception e) {
            log.error("Error filtrando por grado: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Filtrar por grado y sección (admin/profesor)
     * GET /api/estudiantes/grado/{grado}/seccion/{seccion}
     */
    @GetMapping("/grado/{grado}/seccion/{seccion}")
    @PreAuthorize("hasAnyAuthority('teacher', 'admin')")
    public ResponseEntity<ApiResponse<List<EstudianteResponse>>> getByGradoYSeccion(
            @PathVariable String grado,
            @PathVariable String seccion) {
        try {
            List<EstudianteResponse> estudiantes = estudianteService.getByGradoAndSeccion(grado, seccion);
            return ResponseEntity.ok(
                    ApiResponse.success("Estudiantes de " + grado + " - " + seccion, estudiantes)
            );
        } catch (Exception e) {
            log.error("Error filtrando por grado y sección: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }
}