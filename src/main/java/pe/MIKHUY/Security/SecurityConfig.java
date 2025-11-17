package pe.MIKHUY.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Encoder de contraseñas con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Proveedor de autenticación
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Authentication Manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configuración de la cadena de filtros de seguridad
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (no necesario para API REST con JWT)
                .csrf(csrf -> csrf.disable())

                // Configurar CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Manejo de excepciones de autenticación
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                // Política de sesión STATELESS (sin sesiones)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Reglas de autorización
                .authorizeHttpRequests(auth -> auth
                        // ==================== RUTAS PÚBLICAS ====================
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // ==================== RUTAS DE ESTUDIANTES ====================
                        .requestMatchers("/api/estudiantes/**").hasAnyAuthority("student", "admin")
                        .requestMatchers("/api/juegos/**").hasAnyAuthority("student", "teacher", "admin")
                        .requestMatchers("/api/progreso/**").hasAnyAuthority("student", "admin")
                        .requestMatchers("/api/sesiones/**").hasAnyAuthority("student", "admin")
                        .requestMatchers("/api/beneficios/**").hasAnyAuthority("student", "teacher", "admin")
                        .requestMatchers("/api/canjes/**").hasAnyAuthority("student", "admin")
                        .requestMatchers("/api/notificaciones/**").authenticated()
                        .requestMatchers("/api/estudiantes/puntos").hasAuthority("student")

                        // ==================== RUTAS DE PROFESORES ====================
                        .requestMatchers("/api/profesores/**").hasAnyAuthority("teacher", "admin")
                        .requestMatchers("/api/reportes/**").hasAnyAuthority("teacher", "admin")
                        .requestMatchers("/api/analisis/**").hasAnyAuthority("teacher", "admin")

                        // ==================== RUTAS DE ADMIN ====================
                        .requestMatchers("/api/admin/**").hasAuthority("admin")
                        .requestMatchers("/api/usuarios/**").hasAuthority("admin")

                        // ==================== CUALQUIER OTRA RUTA ====================
                        .anyRequest().authenticated()
                );

        // Agregar proveedor de autenticación
        http.authenticationProvider(authenticationProvider());

        // Agregar filtro JWT antes del filtro de autenticación de usuario/contraseña
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuración de CORS para permitir requests desde Angular
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Orígenes permitidos (Angular frontend)
        configuration.setAllowedOrigins(List.of(
                "http://localhost:4200",      // Angular local
                "http://localhost:4201",      // Angular alternativo
                "https://mikhuy-frontend.com" // Producción (cambiar según necesidad)
        ));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS",
                "PATCH"
        ));

        // Headers permitidos
        configuration.setAllowedHeaders(List.of("*"));

        // Permitir credenciales (cookies, headers de autorización)
        configuration.setAllowCredentials(true);

        // Tiempo máximo de cache de la configuración CORS (1 hora)
        configuration.setMaxAge(3600L);

        // Headers expuestos al cliente
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count"
        ));

        // Aplicar configuración a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}