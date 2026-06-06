package pe.MIKHUY.ServiceImplements;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.MIKHUY.DTOs.request.ChangePasswordRequest;
import pe.MIKHUY.DTOs.request.LoginRequest;
import pe.MIKHUY.DTOs.response.AuthResponse;
import pe.MIKHUY.Entities.Estudiante;
import pe.MIKHUY.Entities.Profesor;
import pe.MIKHUY.Entities.Usuario;
import pe.MIKHUY.Repositories.EstudianteRepository;
import pe.MIKHUY.Repositories.ProfesorRepository;
import pe.MIKHUY.Repositories.UsuarioRepository;
import pe.MIKHUY.Security.JwtUtil;
import pe.MIKHUY.Service.AuthService;
import pe.MIKHUY.Service.VerificacionService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImplements implements AuthService {

    private final UsuarioRepository      usuarioRepository;
    private final EstudianteRepository   estudianteRepository;
    private final ProfesorRepository     profesorRepository;
    private final PasswordEncoder        passwordEncoder;
    private final JwtUtil                jwtUtil;
    private final AuthenticationManager  authenticationManager;
    private final VerificacionService    verificacionService;   // ← nuevo

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login para usuario: {}", request.getEmail());

        // 1. Autenticar credenciales con Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase(),
                        request.getPassword()
                )
        );

        // 2. Buscar usuario en BD
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Verificar que esté activo
        if (!usuario.getActivo()) {
            throw new RuntimeException("Usuario inactivo. Contacte al administrador");
        }

        // 4. ── Verificación de cuenta ────────────────────────────────────────
        //    Si el usuario NO está verificado, generamos/renovamos su token
        //    y lo devolvemos en la respuesta para que el frontend redirija a /verify
        if (!usuario.isVerificado()) {
            // Generar token si no tiene uno vigente
            if (usuario.getTokenVerificacion() == null || !usuario.tokenEstaVigente()) {
                verificacionService.generarTokenActivacion(usuario);
                // Recargar para obtener el token recién guardado
                usuario = usuarioRepository.findByEmail(request.getEmail().toLowerCase())
                        .orElseThrow();
            }
            log.warn("Login bloqueado — cuenta no verificada: {}", usuario.getEmail());
            return buildAuthResponseNoVerificado(usuario);
        }

        // 5. Actualizar última conexión
        usuario.setUltimaConexion(LocalDateTime.now());
        usuarioRepository.save(usuario);

        // 6. Generar token JWT
        String token = jwtUtil.generateToken(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRol().name()
        );

        log.info("Login exitoso para usuario: {}", usuario.getEmail());
        return buildAuthResponse(token, usuario);
    }

    @Override
    public boolean verifyToken(String token) {
        return jwtUtil.validateToken(token);
    }

    @Override
    @Transactional
    public void changePassword(String userId, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        Usuario usuario = usuarioRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getOldPassword(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }

        usuario.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(usuario);
        log.info("Contraseña actualizada para: {}", usuario.getEmail());
    }

    @Override
    public AuthResponse refreshToken(String token) {
        String email = jwtUtil.extractUsername(token);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String newToken = jwtUtil.generateToken(
                usuario.getId(), usuario.getEmail(), usuario.getRol().name());

        log.info("Token refrescado para: {}", usuario.getEmail());
        return buildAuthResponse(newToken, usuario);
    }

    // ── Respuesta para cuenta NO verificada (sin JWT, con tokenVerificacion) ─
    private AuthResponse buildAuthResponseNoVerificado(Usuario usuario) {
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name().toLowerCase())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .verificado(false)
                .tokenVerificacion(usuario.getTokenVerificacion())
                .build();

        // No incluimos token JWT — el frontend detecta su ausencia
        return AuthResponse.builder()
                .tokenType("Bearer")
                .user(userInfo)
                .build();
    }

    // ── Respuesta normal (con JWT) ────────────────────────────────────────────
    private AuthResponse buildAuthResponse(String token, Usuario usuario) {
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name().toLowerCase())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .nombreCompleto(usuario.getNombreCompleto())
                .avatarUrl(usuario.getAvatarUrl())
                .verificado(true)
                .build();

        if (usuario.getRol() == Usuario.RolEnum.student) {
            Estudiante est = estudianteRepository.findByUsuarioId(usuario.getId()).orElse(null);
            if (est != null) {
                userInfo.setEstudianteId(est.getId());
                userInfo.setPuntosAcumulados(est.getPuntosAcumulados());
                userInfo.setGrado(est.getGrado());
                userInfo.setSeccion(est.getSeccion());
            }
        } else if (usuario.getRol() == Usuario.RolEnum.teacher) {
            Profesor prof = profesorRepository.findByUsuarioId(usuario.getId()).orElse(null);
            if (prof != null) {
                userInfo.setProfesorId(prof.getId());
                userInfo.setMateria(prof.getMateria());
            }
        }

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userInfo)
                .build();
    }
}