package pe.MIKHUY.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.MIKHUY.Entities.Usuario;
import pe.MIKHUY.Repositories.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificacionService {

    private final UsuarioRepository usuarioRepository;

    // Token válido por 72 horas (cuentas creadas por admin, no urgente)
    private static final long HORAS_VALIDEZ = 72;

    // ── Generar token de activación para un usuario nuevo ───────────────────
    @Transactional
    public String generarTokenActivacion(Usuario usuario) {
        String token = UUID.randomUUID().toString().replace("-", "");
        usuario.setTokenVerificacion(token);
        usuario.setTokenExpira(LocalDateTime.now().plusHours(HORAS_VALIDEZ));
        usuario.setVerificado(false);
        usuarioRepository.save(usuario);
        log.info("Token de activación generado para: {}", usuario.getEmail());
        return token;
    }

    // ── Activar cuenta con token (CP010) ────────────────────────────────────
    @Transactional
    public ResultadoActivacion activarCuenta(String token) {
        // 1. Buscar usuario por token
        Usuario usuario = usuarioRepository.findByTokenVerificacion(token)
                .orElse(null);

        if (usuario == null) {
            log.warn("Token de activación no encontrado: {}", token);
            return ResultadoActivacion.TOKEN_INVALIDO;
        }

        // 2. ¿Ya estaba verificado?
        if (usuario.isVerificado()) {
            log.info("Cuenta ya verificada: {}", usuario.getEmail());
            return ResultadoActivacion.YA_VERIFICADO;
        }

        // 3. ¿Expiró el token? (CP011)
        if (!usuario.tokenEstaVigente()) {
            log.warn("Token expirado para: {}", usuario.getEmail());
            return ResultadoActivacion.TOKEN_EXPIRADO;
        }

        // 4. Activar cuenta
        usuario.setVerificado(true);
        usuario.setTokenVerificacion(null);
        usuario.setTokenExpira(null);
        usuarioRepository.save(usuario);

        log.info("Cuenta activada exitosamente: {}", usuario.getEmail());
        return ResultadoActivacion.EXITO;
    }

    // ── Reenviar token (botón "Reenviar correo" del CP011) ──────────────────
    @Transactional
    public String reenviarToken(String token) {
        // Buscar por token expirado
        Usuario usuario = usuarioRepository.findByTokenVerificacion(token)
                .orElse(null);

        if (usuario == null) {
            return null;
        }

        // Generar nuevo token
        String nuevoToken = UUID.randomUUID().toString().replace("-", "");
        usuario.setTokenVerificacion(nuevoToken);
        usuario.setTokenExpira(LocalDateTime.now().plusHours(HORAS_VALIDEZ));
        usuarioRepository.save(usuario);

        log.info("Token reenviado para: {}", usuario.getEmail());
        return nuevoToken;
    }

    // ── Obtener o crear URL de activación (para panel admin) ───────────────
    @Transactional
    public String getOrCreateActivationUrl(String email, String frontendBaseUrl) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) return null;

        // Si ya está verificado, devolver null para que el controller lo maneje
        if (usuario.isVerificado()) return "YA_VERIFICADO";

        // Si no tiene token vigente, generar uno nuevo
        if (usuario.getTokenVerificacion() == null || !usuario.tokenEstaVigente()) {
            generarTokenActivacion(usuario);
        }

        return buildActivationUrl(usuario.getTokenVerificacion(), frontendBaseUrl);
    }

    // ── Obtener URL de activación (para mostrarla en el panel admin) ────────
    public String buildActivationUrl(String token, String frontendBaseUrl) {
        return frontendBaseUrl + "/verify?token=" + token;
    }

    // ── Enum de resultados ──────────────────────────────────────────────────
    public enum ResultadoActivacion {
        EXITO,
        TOKEN_INVALIDO,
        TOKEN_EXPIRADO,
        YA_VERIFICADO
    }
}