package pe.MIKHUY.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pe.MIKHUY.Entities.Usuario;
import pe.MIKHUY.Repositories.UsuarioRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Utilidad para obtener información del usuario autenticado actualmente
 */
@Component
@RequiredArgsConstructor
public class CurrentUserUtil {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    /**
     * Obtener el email del usuario autenticado
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }

        return null;
    }

    /**
     * Obtener el ID del usuario autenticado desde el token JWT
     */
    public UUID getCurrentUserId(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            return jwtUtil.extractUserId(token);
        }
        return null;
    }

    /**
     * Obtener el rol del usuario autenticado
     */
    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getAuthorities() != null &&
                !authentication.getAuthorities().isEmpty()) {
            return authentication.getAuthorities().iterator().next().getAuthority();
        }

        return null;
    }

    /**
     * Obtener la entidad Usuario completa del usuario autenticado
     */
    public Optional<Usuario> getCurrentUser() {
        String email = getCurrentUserEmail();

        if (email != null) {
            return usuarioRepository.findByEmail(email);
        }

        return Optional.empty();
    }

    /**
     * Verificar si el usuario actual es administrador
     */
    public boolean isAdmin() {
        return "admin".equals(getCurrentUserRole());
    }

    /**
     * Verificar si el usuario actual es profesor
     */
    public boolean isTeacher() {
        String role = getCurrentUserRole();
        return "teacher".equals(role) || "admin".equals(role);
    }

    /**
     * Verificar si el usuario actual es estudiante
     */
    public boolean isStudent() {
        return "student".equals(getCurrentUserRole());
    }

    /**
     * Verificar si hay un usuario autenticado
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }
}