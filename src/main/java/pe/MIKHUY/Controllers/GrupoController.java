package pe.MIKHUY.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.MIKHUY.DTOs.ApiResponse;
import pe.MIKHUY.DTOs.request.CrearGrupoRequest;
import pe.MIKHUY.DTOs.response.GrupoResumenResponse;
import pe.MIKHUY.Security.CurrentUserUtil;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/grupos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "https://mikhuy-front.web.app", "https://mikhuy-front.firebaseapp.com"})
public class GrupoController {

    private final pe.MIKHUY.Service.GrupoService grupoService;
    private final CurrentUserUtil currentUserUtil;

    @PostMapping
    @PreAuthorize("hasAuthority('teacher')")
    public ResponseEntity<ApiResponse<GrupoResumenResponse>> crear(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CrearGrupoRequest request) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            return ResponseEntity.ok(ApiResponse.success("Grupo creado",
                    grupoService.crearGrupo(usuarioId, request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('teacher')")
    public ResponseEntity<ApiResponse<List<GrupoResumenResponse>>> getMisGrupos(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID usuarioId = currentUserUtil.getCurrentUserId(authHeader);
            return ResponseEntity.ok(ApiResponse.success("Grupos obtenidos",
                    grupoService.getGruposDelProfesor(usuarioId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('teacher')")
    public ResponseEntity<ApiResponse<GrupoResumenResponse>> getDetalle(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Grupo obtenido",
                    grupoService.getGrupoDetalle(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('teacher')")
    public ResponseEntity<ApiResponse<GrupoResumenResponse>> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody CrearGrupoRequest request) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Grupo actualizado",
                    grupoService.actualizarGrupo(id, request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('teacher')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable UUID id) {
        try {
            grupoService.eliminarGrupo(id);
            return ResponseEntity.ok(ApiResponse.success("Grupo eliminado", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}