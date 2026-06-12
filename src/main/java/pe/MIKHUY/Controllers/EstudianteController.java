package pe.MIKHUY.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.MIKHUY.DTOs.ApiResponse;
import pe.MIKHUY.DTOs.request.EnviarNotificacionRequest;
import pe.MIKHUY.DTOs.request.UpdateProfileRequest;
import pe.MIKHUY.DTOs.response.EstadisticasEstudianteResponse;
import pe.MIKHUY.DTOs.response.EstudianteResponse;
import pe.MIKHUY.DTOs.response.RankingResponse;
import pe.MIKHUY.Entities.Estudiante;
import pe.MIKHUY.Repositories.EstudianteRepository;
import pe.MIKHUY.Security.CurrentUserUtil;
import pe.MIKHUY.Service.AmigoService;
import pe.MIKHUY.Service.EstudianteService;
import pe.MIKHUY.DTOs.request.AmistadRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "https://mikhuy-front.web.app", "https://mikhuy-front.firebaseapp.com"})
public class EstudianteController {
    private final EstudianteService estudianteService;
    private final CurrentUserUtil currentUserUtil;
    private final AmigoService amigoService;
    private final EstudianteRepository estudianteRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // Endpoints existentes (sin cambios)
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/perfil")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<EstudianteResponse>> getMiPerfil(
            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("📡 GET /api/estudiantes/perfil");
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse estudiante = estudianteService.getPerfilByUsuarioId(usuarioId);
            log.info("✅ Perfil obtenido para: {}", estudiante.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Perfil obtenido", estudiante));
        } catch (Exception e) {
            log.error("❌ Error obteniendo perfil: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error obteniendo perfil: " + e.getMessage()));
        }
    }

    @PatchMapping("/perfil")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<EstudianteResponse>> patchMiPerfil(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UpdateProfileRequest request) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse estudiante = estudianteService.updatePerfil(usuarioId, request);
            return ResponseEntity.ok(ApiResponse.success("Perfil actualizado", estudiante));
        } catch (Exception e) {
            log.error("❌ Error actualizando perfil: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error actualizando perfil: " + e.getMessage()));
        }
    }

    @GetMapping("/puntos")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<Integer>> getMisPuntos(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse estudiante = estudianteService.getPerfilByUsuarioId(usuarioId);
            Integer puntos = estudiante.getPuntosAcumulados() != null ? estudiante.getPuntosAcumulados() : 0;
            return ResponseEntity.ok(ApiResponse.success("Puntos obtenidos correctamente", puntos));
        } catch (Exception e) {
            log.error("❌ Error obteniendo puntos: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error obteniendo puntos: " + e.getMessage()));
        }
    }

    @GetMapping("/estadisticas")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<EstadisticasEstudianteResponse>> getMisEstadisticas(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse estudiante = estudianteService.getPerfilByUsuarioId(usuarioId);
            EstadisticasEstudianteResponse estadisticas = estudianteService.getEstadisticas(estudiante.getId());
            return ResponseEntity.ok(ApiResponse.success("Estadísticas obtenidas", estadisticas));
        } catch (Exception e) {
            log.error("❌ Error obteniendo estadísticas: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error obteniendo estadísticas: " + e.getMessage()));
        }
    }

    @GetMapping("/ranking")
    @PreAuthorize("hasAnyAuthority('student', 'teacher', 'admin')")
    public ResponseEntity<ApiResponse<RankingResponse>> getRanking(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            UUID estudianteId = null;
            if (authHeader != null && currentUserUtil.isStudent()) {
                UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
                EstudianteResponse estudiante = estudianteService.getPerfilByUsuarioId(usuarioId);
                estudianteId = estudiante.getId();
            }
            RankingResponse ranking = estudianteService.getRanking(estudianteId);
            return ResponseEntity.ok(ApiResponse.success("Ranking obtenido", ranking));
        } catch (Exception e) {
            log.error("❌ Error obteniendo ranking: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error obteniendo ranking: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('teacher', 'admin')")
    public ResponseEntity<ApiResponse<EstudianteResponse>> getById(@PathVariable UUID id) {
        try {
            EstudianteResponse estudiante = estudianteService.getById(id);
            return ResponseEntity.ok(ApiResponse.success("Estudiante encontrado", estudiante));
        } catch (Exception e) {
            log.error("❌ Error obteniendo estudiante: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('teacher')")
    public ResponseEntity<ApiResponse<List<EstudianteResponse>>> getAll(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            List<Estudiante> asignados = estudianteRepository
                    .findByProfesorUsuarioId(usuarioId);

            List<EstudianteResponse> responses = asignados.stream()
                    .map(e -> estudianteService.getById(e.getId()))
                    .toList();

            return ResponseEntity.ok(ApiResponse.success(
                    responses.isEmpty() ? "No tienes estudiantes asignados" : "Lista de estudiantes",
                    responses));

        } catch (Exception e) {
            log.error("❌ Error listando estudiantes: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Endpoints nuevos: sistema de amigos
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Devuelve los compañeros del mismo grado y sección, excluyendo al propio estudiante.
     * GET /api/estudiantes/companeros
     */
    @GetMapping("/companeros")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<List<EstudianteResponse>>> getCompaneros(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            List<EstudianteResponse> companeros = amigoService.getCompanerosMismoGrupo(usuarioId);
            log.info("✅ Compañeros obtenidos para usuario: {}", usuarioId);
            return ResponseEntity.ok(ApiResponse.success("Compañeros obtenidos", companeros));
        } catch (Exception e) {
            log.error("❌ Error obteniendo compañeros: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error obteniendo compañeros: " + e.getMessage()));
        }
    }

    /**
     * Envía una notificación de amistad a otro estudiante.
     * Tipos: amistad_solicitud | amistad_aceptada | amistad_rechazada
     * POST /api/estudiantes/notificar-amigo
     */
    @PostMapping("/notificar-amigo")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<Void>> notificarAmigo(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody EnviarNotificacionRequest request) {
        try {
            UUID remitenteUsuarioId = currentUserUtil.getCurrentUserId(authHeader);
            amigoService.enviarNotificacion(remitenteUsuarioId, request);
            log.info("✅ Notificación '{}' enviada al estudiante: {}", request.getTipo(), request.getDestinatarioEstudianteId());
            return ResponseEntity.ok(ApiResponse.success("Notificación enviada", null));
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Solicitud inválida: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("❌ Error enviando notificación: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error enviando notificación: " + e.getMessage()));
        }
    }

    /**
     * Lista los amigos confirmados (datos completos) del estudiante autenticado.
     * GET /api/estudiantes/amigos
     */
    @GetMapping("/amigos")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<List<EstudianteResponse>>> getMisAmigos(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            EstudianteResponse yo = estudianteService.getPerfilByUsuarioId(usuarioId);
            List<EstudianteResponse> amigos = amigoService.getAmigosIds(yo.getId()).stream()
                    .map(estudianteService::getById)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Amigos obtenidos", amigos));
        } catch (Exception e) {
            log.error("❌ Error obteniendo amigos: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }
}