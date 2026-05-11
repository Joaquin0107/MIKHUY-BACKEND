package pe.MIKHUY.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 🔹 Ignorar rutas públicas
        if (path.startsWith("/api/chatbot") ||
                path.startsWith("/api/auth") ||
                path.startsWith("/swagger") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/api/public")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🔹 Procesar JWT
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String username = jwtUtil.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(token, userDetails)) {
                        String rol = jwtUtil.extractRole(token);
                        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(rol));

                        log.info("🔐 Método HTTP: {}", request.getMethod());
                        log.info("🔐 URI: {}", request.getRequestURI());
                        log.info("🔐 Rol extraído: {}", rol);
                        log.info("🔐 Authorities: {}", authorities);

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        log.info("✅ Usuario autenticado: {} con rol: {}", username, rol);
                    } else {
                        log.warn("❌ Token inválido para usuario: {}", username);
                    }
                }
            } catch (Exception e) {
                log.error("❌ Error procesando token: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}