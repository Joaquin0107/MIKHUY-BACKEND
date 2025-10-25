package pe.MIKHUY.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.MIKHUY.DTOs.ApiResponse;
import pe.MIKHUY.DTOs.response.BeneficioResponse;
import pe.MIKHUY.DTOs.response.EstudianteResponse;
import pe.MIKHUY.Security.CurrentUserUtil;
import pe.MIKHUY.Service.BeneficioService;
import pe.MIKHUY.Service.EstudianteService;

import java.util.List;
import java.util.UUID;

/**
 * Controlador de Beneficios
 * Endpoints: /api/beneficios/**
 */
@RestController
@RequestMapping("/beneficios")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class BeneficioController {

    private final BeneficioService beneficioService;
    private final EstudianteService estudianteService;
    private final CurrentUserUtil currentUserUtil;

    /**
     * Obtener todos los beneficios activos
     * GET /api/beneficios
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('student', 'teacher', 'admin')")
    public ResponseEntity<ApiResponse<List<BeneficioResponse>>> getAllActive() {
        try {
            List<BeneficioResponse> beneficios = beneficioService.getAllActive();
            return ResponseEntity.ok(
                    ApiResponse.success("Beneficios obtenidos", beneficios)
            );
        } catch (Exception e) {
            log.error("Error obteniendo beneficios: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtener beneficios disponibles para mí (con stock y puntos suficientes)
     * GET /api/beneficios/disponibles
     */
    @GetMapping("/disponibles")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<List<BeneficioResponse>>> getMisDisponibles(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse estudiante = estudianteService.getPerfilByUsuarioId(usuarioId);

            List<BeneficioResponse> beneficios = beneficioService.getDisponiblesParaEstudiante(estudiante.getId());

            return ResponseEntity.ok(
                    ApiResponse.success("Beneficios disponibles", beneficios)
            );
        } catch (Exception e) {
            log.error("Error obteniendo beneficios disponibles: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtener beneficio por ID
     * GET /api/beneficios/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('student', 'teacher', 'admin')")
    public ResponseEntity<ApiResponse<BeneficioResponse>> getById(@PathVariable UUID id) {
        try {
            BeneficioResponse beneficio = beneficioService.getById(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Beneficio encontrado", beneficio)
            );
        } catch (Exception e) {
            log.error("Error obteniendo beneficio: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtener beneficios por categoría
     * GET /api/beneficios/categoria/{categoria}
     */
    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("hasAnyAuthority('student', 'teacher', 'admin')")
    public ResponseEntity<ApiResponse<List<BeneficioResponse>>> getByCategoria(@PathVariable String categoria) {
        try {
            List<BeneficioResponse> beneficios = beneficioService.getByCategoria(categoria);
            return ResponseEntity.ok(
                    ApiResponse.success("Beneficios de categoría " + categoria, beneficios)
            );
        } catch (Exception e) {
            log.error("Error obteniendo beneficios por categoría: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }
}