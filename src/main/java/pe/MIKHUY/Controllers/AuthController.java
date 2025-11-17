package pe.MIKHUY.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.MIKHUY.DTOs.ApiResponse;
import pe.MIKHUY.DTOs.request.ChangePasswordRequest;
import pe.MIKHUY.DTOs.request.LoginRequest;
import pe.MIKHUY.DTOs.response.AuthResponse;
import pe.MIKHUY.Security.CurrentUserUtil;
import pe.MIKHUY.Service.AuthService;

/**
 * Controlador de Autenticación
 * Endpoints: /api/auth/**
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private final AuthService authService;
    private final CurrentUserUtil currentUserUtil;

    /**
     * Login de usuario
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("Solicitud de login para: {}", request.getEmail());
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Login exitoso", response));
        } catch (Exception e) {
            log.error("Error en login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Credenciales inválidas: " + e.getMessage()));
        }
    }

    /**
     * Verificar token
     * GET /api/auth/verify
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyToken(
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Token no proporcionado"));
            }

            String token = authHeader.substring(7);
            boolean isValid = authService.verifyToken(token);

            if (isValid) {
                return ResponseEntity.ok(ApiResponse.success("Token válido", null));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Token inválido o expirado"));
            }
        } catch (Exception e) {
            log.error("Error verificando token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Error verificando token"));
        }
    }

    /**
     * Cambiar contraseña
     * PUT /api/auth/change-password
     */
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            String userId = currentUserUtil.getCurrentUserId(authHeader).toString();

            authService.changePassword(userId, request);

            return ResponseEntity.ok(
                    ApiResponse.success("Contraseña actualizada exitosamente", null)
            );
        } catch (IllegalArgumentException e) {
            log.error("Error cambiando contraseña: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error cambiando contraseña: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error en el servidor"));
        }
    }

    /**
     * Refrescar token
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Token no proporcionado"));
            }

            String token = authHeader.substring(7);
            AuthResponse response = authService.refreshToken(token);

            return ResponseEntity.ok(
                    ApiResponse.success("Token refrescado", response)
            );
        } catch (Exception e) {
            log.error("Error refrescando token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Error refrescando token"));
        }
    }

    /**
     * Logout (cliente elimina el token)
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // En JWT stateless, el logout se maneja en el cliente eliminando el token
        return ResponseEntity.ok(
                ApiResponse.success("Logout exitoso", null)
        );
    }
}