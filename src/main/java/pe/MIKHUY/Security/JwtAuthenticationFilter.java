package pe.MIKHUY.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // Obtener el header Authorization
            String authHeader = request.getHeader("Authorization");

            // Verificar si el header existe y tiene el formato correcto
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // Extraer el token (remover "Bearer ")
                String token = authHeader.substring(7);

                // Extraer el username del token
                String username = jwtUtil.extractUsername(token);

                // Si hay username y no hay autenticación previa
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // Cargar detalles del usuario
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Validar el token
                    if (jwtUtil.validateToken(token, userDetails)) {
                        // Crear objeto de autenticación
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        // Establecer detalles adicionales
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        // Establecer la autenticación en el contexto de seguridad
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        log.debug("Usuario autenticado: {}", username);
                    } else {
                        log.warn("Token inválido para usuario: {}", username);
                    }
                }
            }
        } catch (Exception e) {
            log.error("No se pudo establecer la autenticación del usuario: {}", e.getMessage());
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // No filtrar las rutas públicas
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") ||
                path.startsWith("/api/public/");
    }
}