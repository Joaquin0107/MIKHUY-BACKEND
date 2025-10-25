package pe.MIKHUY.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.MIKHUY.DTOs.ApiResponse;
import pe.MIKHUY.DTOs.request.*;
import pe.MIKHUY.DTOs.response.EstudianteResponse;
import pe.MIKHUY.DTOs.response.SesionJuegoResponse;
import pe.MIKHUY.Security.CurrentUserUtil;
import pe.MIKHUY.Service.EstudianteService;
import pe.MIKHUY.Service.SesionJuegoService;

import java.util.List;
import java.util.UUID;

/**
 * Controlador de Sesiones de Juego
 * Endpoints: /api/sesiones/**
 */
@RestController
@RequestMapping("/sesiones")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class SesionJuegoController {

    private final SesionJuegoService sesionJuegoService;
    private final EstudianteService estudianteService;
    private final CurrentUserUtil currentUserUtil;

    /**
     * Iniciar sesión de juego
     * POST /api/sesiones/iniciar
     */
    @PostMapping("/iniciar")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<SesionJuegoResponse>> iniciarSesion(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody IniciarSesionJuegoRequest request) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse estudiante = estudianteService.getPerfilByUsuarioId(usuarioId);

            SesionJuegoResponse sesion = sesionJuegoService.iniciarSesion(estudiante.getId(), request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Sesión iniciada", sesion));
        } catch (Exception e) {
            log.error("Error iniciando sesión: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Finalizar sesión de juego
     * PUT /api/sesiones/finalizar
     */
    @PutMapping("/finalizar")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<SesionJuegoResponse>> finalizarSesion(
            @Valid @RequestBody FinalizarSesionJuegoRequest request) {
        try {
            SesionJuegoResponse sesion = sesionJuegoService.finalizarSesion(request);

            return ResponseEntity.ok(
                    ApiResponse.success("Sesión finalizada", sesion)
            );
        } catch (Exception e) {
            log.error("Error finalizando sesión: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Guardar respuesta de Desafío Nutrimental
     * POST /api/sesiones/nutrimental/respuesta
     */
    @PostMapping("/nutrimental/respuesta")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<Void>> guardarRespuestaNutrimental(
            @Valid @RequestBody GuardarNutrimentalRespuestaRequest request) {
        try {
            sesionJuegoService.guardarRespuestaNutrimental(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Respuesta guardada", null));
        } catch (Exception e) {
            log.error("Error guardando respuesta: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Guardar registro de Reto 7 Días
     * POST /api/sesiones/reto7dias/registro
     */
    @PostMapping("/reto7dias/registro")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<Void>> guardarRegistroReto7Dias(
            @Valid @RequestBody GuardarReto7DiasRegistroRequest request) {
        try {
            sesionJuegoService.guardarRegistroReto7Dias(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Registro guardado", null));
        } catch (Exception e) {
            log.error("Error guardando registro: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Guardar respuesta de Coach Exprés
     * POST /api/sesiones/coach/respuesta
     */
    @PostMapping("/coach/respuesta")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<Void>> guardarRespuestaCoach(
            @Valid @RequestBody GuardarCoachRespuestaRequest request) {
        try {
            sesionJuegoService.guardarRespuestaCoach(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Respuesta guardada", null));
        } catch (Exception e) {
            log.error("Error guardando respuesta: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtener mis sesiones
     * GET /api/sesiones/mis-sesiones
     */
    @GetMapping("/mis-sesiones")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<List<SesionJuegoResponse>>> getMisSesiones(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse estudiante = estudianteService.getPerfilByUsuarioId(usuarioId);

            List<SesionJuegoResponse> sesiones = sesionJuegoService.getSesionesByEstudiante(estudiante.getId());

            return ResponseEntity.ok(
                    ApiResponse.success("Sesiones obtenidas", sesiones)
            );
        } catch (Exception e) {
            log.error("Error obteniendo sesiones: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }
}