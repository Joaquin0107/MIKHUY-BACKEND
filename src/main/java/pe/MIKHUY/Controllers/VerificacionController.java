package pe.MIKHUY.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.MIKHUY.DTOs.ApiResponse;
import pe.MIKHUY.Service.VerificacionService;
import pe.MIKHUY.Service.VerificacionService.ResultadoActivacion;

/**
 * Endpoints de verificación de cuenta
 * GET  /api/auth/activate?token=...   → CP010 éxito / CP011 expirado
 * POST /api/auth/resend?token=...     → reenviar token (CP011 botón)
 * GET  /api/auth/activation-url?email=... → obtener URL para panel admin
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class VerificacionController {

    private final VerificacionService verificacionService;

    // ── CP010 + CP011: Activar cuenta con token ─────────────────────────────
    @GetMapping("/activate")
    public ResponseEntity<ApiResponse<Void>> activarCuenta(
            @RequestParam("token") String token) {

        ResultadoActivacion resultado = verificacionService.activarCuenta(token);

        return switch (resultado) {

            case EXITO -> ResponseEntity.ok(
                    ApiResponse.success("¡Verificación exitosa! Tu cuenta ha sido activada correctamente.", null));

            case YA_VERIFICADO -> ResponseEntity.ok(
                    ApiResponse.success("Tu cuenta ya estaba verificada. Puedes iniciar sesión.", null));

            case TOKEN_EXPIRADO -> ResponseEntity.status(HttpStatus.GONE)
                    .body(ApiResponse.error("EXPIRADO: El enlace de verificación ha expirado."));

            case TOKEN_INVALIDO -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("El enlace de verificación no es válido."));
        };
    }

    // ── CP011: Reenviar enlace de activación ────────────────────────────────
    @PostMapping("/resend")
    public ResponseEntity<ApiResponse<String>> reenviarActivacion(
            @RequestParam("token") String tokenAntiguo) {

        String nuevoToken = verificacionService.reenviarToken(tokenAntiguo);

        if (nuevoToken == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("No se encontró la cuenta asociada a este enlace."));
        }

        // En producción real aquí se enviaría el correo.
        // En MIKHUY devolvemos el nuevo token para construir la URL manualmente.
        return ResponseEntity.ok(
                ApiResponse.success("Nuevo enlace generado correctamente.", nuevoToken));
    }

    // ── Panel admin: obtener URL de activación de un usuario ────────────────
    @GetMapping("/activation-url")
    public ResponseEntity<ApiResponse<String>> getActivationUrl(
            @RequestParam("email") String email,
            @RequestParam(value = "base", defaultValue = "https://mikhuy-front.web.app") String base) {

        // Buscar usuario y generar token si no tiene
        // (delegado al servicio para mantener el controlador limpio)
        String url = verificacionService.getOrCreateActivationUrl(email, base);

        if (url == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Usuario no encontrado: " + email));
        }

        return ResponseEntity.ok(ApiResponse.success("URL de activación generada.", url));
    }
}