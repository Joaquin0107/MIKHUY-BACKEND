package pe.MIKHUY.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.MIKHUY.DTOs.ApiResponse;
import pe.MIKHUY.DTOs.response.EstudianteResponse;
import pe.MIKHUY.DTOs.response.JuegoResponse;
import pe.MIKHUY.Security.CurrentUserUtil;
import pe.MIKHUY.Service.EstudianteService;
import pe.MIKHUY.Service.JuegoService;

import java.util.List;
import java.util.UUID;

/**
 * Controlador de Juegos
 * Endpoints: /api/juegos/**
 */
@RestController
@RequestMapping("/juegos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class JuegoController {

    private final JuegoService juegoService;
    private final EstudianteService estudianteService;
    private final CurrentUserUtil currentUserUtil;

    /**
     * Obtener todos los juegos activos
     * GET /api/juegos
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('student', 'teacher', 'admin')")
    public ResponseEntity<ApiResponse<List<JuegoResponse>>> getAllActive() {
        try {
            List<JuegoResponse> juegos = juegoService.getAllActive();
            return ResponseEntity.ok(
                    ApiResponse.success("Juegos obtenidos", juegos)
            );
        } catch (Exception e) {
            log.error("Error obteniendo juegos: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtener juegos con progreso del estudiante autenticado
     * GET /api/juegos/mi-progreso
     */
    @GetMapping("/mi-progreso")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<List<JuegoResponse>>> getMisJuegosConProgreso(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse estudiante = estudianteService.getPerfilByUsuarioId(usuarioId);

            List<JuegoResponse> juegos = juegoService.getJuegosConProgreso(estudiante.getId());

            return ResponseEntity.ok(
                    ApiResponse.success("Juegos con progreso obtenidos", juegos)
            );
        } catch (Exception e) {
            log.error("Error obteniendo juegos con progreso: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtener juego por ID
     * GET /api/juegos/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('student', 'teacher', 'admin')")
    public ResponseEntity<ApiResponse<JuegoResponse>> getById(@PathVariable UUID id) {
        try {
            JuegoResponse juego = juegoService.getById(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Juego encontrado", juego)
            );
        } catch (Exception e) {
            log.error("Error obteniendo juego: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtener juegos por categoría
     * GET /api/juegos/categoria/{categoria}
     */
    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("hasAnyAuthority('student', 'teacher', 'admin')")
    public ResponseEntity<ApiResponse<List<JuegoResponse>>> getByCategoria(@PathVariable String categoria) {
        try {
            List<JuegoResponse> juegos = juegoService.getByCategoria(categoria);
            return ResponseEntity.ok(
                    ApiResponse.success("Juegos de categoría " + categoria, juegos)
            );
        } catch (Exception e) {
            log.error("Error obteniendo juegos por categoría: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }
}