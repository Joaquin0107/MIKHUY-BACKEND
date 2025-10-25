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
import pe.MIKHUY.DTOs.request.RegisterStudentRequest;
import pe.MIKHUY.DTOs.response.AuthResponse;
import pe.MIKHUY.Entities.Estudiante;
import pe.MIKHUY.Entities.Profesor;
import pe.MIKHUY.Entities.Usuario;
import pe.MIKHUY.Repositories.EstudianteRepository;
import pe.MIKHUY.Repositories.ProfesorRepository;
import pe.MIKHUY.Repositories.UsuarioRepository;
import pe.MIKHUY.Security.JwtUtil;
import pe.MIKHUY.Service.AuthService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImplements implements AuthService {
    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final ProfesorRepository profesorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login para usuario: {}", request.getEmail());

        // Autenticar usuario con Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase(),
                        request.getPassword()
                )
        );

        // Buscar usuario en BD
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que esté activo
        if (!usuario.getActivo()) {
            throw new RuntimeException("Usuario inactivo. Contacte al administrador");
        }

        // Actualizar última conexión
        usuario.setUltimaConexion(LocalDateTime.now());
        usuarioRepository.save(usuario);

        // Generar token JWT
        String token = jwtUtil.generateToken(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRol().name()
        );

        log.info("Login exitoso para usuario: {}", usuario.getEmail());

        // Construir respuesta según rol
        return buildAuthResponse(token, usuario);
    }

    @Override
    @Transactional
    public AuthResponse registerStudent(RegisterStudentRequest request) {
        log.info("Intento de registro para email: {}", request.getEmail());

        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail().toLowerCase());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(Usuario.RolEnum.student);
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setTelefono(request.getTelefono());
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());

        usuario = usuarioRepository.save(usuario);

        // Crear estudiante
        Estudiante estudiante = new Estudiante();
        estudiante.setUsuario(usuario);
        estudiante.setEdad(request.getEdad());
        estudiante.setGrado(request.getGrado());
        estudiante.setSeccion(request.getSeccion());
        estudiante.setTalla(request.getTalla());
        estudiante.setPeso(request.getPeso());
        estudiante.setPuntosAcumulados(0);
        estudiante.setFechaRegistro(LocalDateTime.now());

        estudianteRepository.save(estudiante);

        // Generar token
        String token = jwtUtil.generateToken(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRol().name()
        );

        log.info("Registro exitoso para estudiante: {}", usuario.getEmail());

        // Construir respuesta
        return buildAuthResponse(token, usuario);
    }

    @Override
    public boolean verifyToken(String token) {
        return jwtUtil.validateToken(token);
    }

    @Override
    @Transactional
    public void changePassword(String userId, ChangePasswordRequest request) {
        log.info("Intento de cambio de contraseña para usuario ID: {}", userId);

        // Verificar que las contraseñas nuevas coincidan
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        // Buscar usuario
        Usuario usuario = usuarioRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(request.getOldPassword(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }

        // Actualizar contraseña
        usuario.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(usuario);

        log.info("Contraseña actualizada exitosamente para usuario: {}", usuario.getEmail());
    }

    @Override
    public AuthResponse refreshToken(String token) {
        // Extraer información del token actual
        String email = jwtUtil.extractUsername(token);

        // Buscar usuario
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Generar nuevo token
        String newToken = jwtUtil.generateToken(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRol().name()
        );

        log.info("Token refrescado para usuario: {}", usuario.getEmail());

        return buildAuthResponse(newToken, usuario);
    }

    /**
     * Construir respuesta de autenticación según el rol del usuario
     */
    private AuthResponse buildAuthResponse(String token, Usuario usuario) {
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .nombreCompleto(usuario.getNombreCompleto())
                .avatarUrl(usuario.getAvatarUrl())
                .build();

        // Agregar información específica según el rol
        if (usuario.getRol() == Usuario.RolEnum.student) {
            Estudiante estudiante = estudianteRepository.findByUsuarioId(usuario.getId())
                    .orElse(null);
            if (estudiante != null) {
                userInfo.setEstudianteId(estudiante.getId());
                userInfo.setPuntosAcumulados(estudiante.getPuntosAcumulados());
                userInfo.setGrado(estudiante.getGrado());
                userInfo.setSeccion(estudiante.getSeccion());
            }
        } else if (usuario.getRol() == Usuario.RolEnum.teacher) {
            Profesor profesor = profesorRepository.findByUsuarioId(usuario.getId())
                    .orElse(null);
            if (profesor != null) {
                userInfo.setProfesorId(profesor.getId());
                userInfo.setMateria(profesor.getMateria());
            }
        }

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userInfo)
                .build();
    }
}