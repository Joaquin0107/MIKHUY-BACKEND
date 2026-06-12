package pe.MIKHUY.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.MIKHUY.DTOs.ApiResponse;
import pe.MIKHUY.DTOs.response.EstudianteResponse;
import pe.MIKHUY.Security.CurrentUserUtil;
import pe.MIKHUY.Service.AmigoService;
import pe.MIKHUY.Service.EstudianteService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/amistades")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "https://mikhuy-front.web.app", "https://mikhuy-front.firebaseapp.com"})
public class AmistadController {

    private final AmigoService amigoService;
    private final EstudianteService estudianteService;
    private final CurrentUserUtil currentUserUtil;

    /** POST /api/amistades/solicitar/{receptorId} */
    @PostMapping("/solicitar/{receptorId}")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<Void>> solicitar(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID receptorId) {
        try {
            UUID miId = miEstudianteId(authHeader);
            amigoService.enviarSolicitudAmistad(miId, receptorId);
            return ResponseEntity.ok(ApiResponse.success("Solicitud enviada", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /** POST /api/amistades/aceptar/{solicitanteId} */
    @PostMapping("/aceptar/{solicitanteId}")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<Void>> aceptar(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID solicitanteId) {
        try {
            UUID miId = miEstudianteId(authHeader);
            amigoService.aceptarSolicitud(miId, solicitanteId);
            return ResponseEntity.ok(ApiResponse.success("Amistad aceptada", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /** DELETE /api/amistades/{otroId} -- rechaza solicitud o elimina amigo */
    @DeleteMapping("/{otroId}")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID otroId) {
        try {
            UUID miId = miEstudianteId(authHeader);
            amigoService.rechazarOEliminar(miId, otroId);
            return ResponseEntity.ok(ApiResponse.success("Eliminado", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /** GET /api/amistades -- amigos confirmados (con datos completos) */
    @GetMapping
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<List<EstudianteResponse>>> getMisAmigos(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID miId = miEstudianteId(authHeader);
            List<EstudianteResponse> amigos = amigoService.getAmigosIds(miId).stream()
                    .map(estudianteService::getById)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Amigos obtenidos", amigos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /** GET /api/amistades/solicitudes-recibidas */
    @GetMapping("/solicitudes-recibidas")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<List<EstudianteResponse>>> getSolicitudesRecibidas(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID miId = miEstudianteId(authHeader);
            return ResponseEntity.ok(ApiResponse.success("Solicitudes obtenidas",
                    amigoService.getSolicitudesRecibidas(miId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /** GET /api/amistades/solicitudes-enviadas -- lista de IDs */
    @GetMapping("/solicitudes-enviadas")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<List<UUID>>> getSolicitudesEnviadas(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID miId = miEstudianteId(authHeader);
            return ResponseEntity.ok(ApiResponse.success("Enviadas obtenidas",
                    amigoService.getSolicitudesEnviadasIds(miId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    /** GET /api/amistades/estado/{otroId} -- estado de relación con otro estudiante */
    @GetMapping("/estado/{otroId}")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<ApiResponse<Map<String, String>>> getEstado(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID otroId) {
        try {
            UUID miId = miEstudianteId(authHeader);
            String estado = amigoService.getEstadoRelacion(miId, otroId);
            return ResponseEntity.ok(ApiResponse.success("Estado obtenido", Map.of("estado", estado)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    private UUID miEstudianteId(String authHeader) {
        UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
        return estudianteService.getPerfilByUsuarioId(usuarioId).getId();
    }
}