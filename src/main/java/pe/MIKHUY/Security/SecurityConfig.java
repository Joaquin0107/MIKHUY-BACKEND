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

    // ===========================
    // Password Encoder
    // ===========================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ===========================
    // Authentication Provider
    // ===========================
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // ===========================
    // Authentication Manager
    // ===========================
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // ===========================
    // Security Filter Chain
    // ===========================
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/api/chatbot/consulta").permitAll()
                        .requestMatchers("/health").permitAll()  // <-- Health
                        .requestMatchers("/").permitAll()        // <-- Raíz pública
                        // Email - requiere autenticación
                        .requestMatchers("/api/email/send-with-pdf").permitAll()
                        .requestMatchers("/api/email/**").authenticated()
                        // Resto de rutas según roles
                        .requestMatchers("/api/estudiantes/**").hasAnyAuthority("student", "admin")
                        .requestMatchers("/api/juegos/**").hasAnyAuthority("student", "teacher", "admin")
                        .requestMatchers("/api/progreso/**").hasAnyAuthority("student", "admin")
                        .requestMatchers("/api/sesiones/**").hasAnyAuthority("student", "admin")
                        .requestMatchers("/api/beneficios/**").hasAnyAuthority("student", "teacher", "admin")
                        .requestMatchers("/api/canjes/**").hasAnyAuthority("student", "admin")
                        .requestMatchers("/api/notificaciones/**").authenticated()
                        .requestMatchers("/api/estudiantes/puntos").hasAuthority("student")
                        .requestMatchers("/api/profesores/**").hasAnyAuthority("teacher", "admin")
                        .requestMatchers("/api/reportes/**").hasAnyAuthority("teacher", "admin")
                        .requestMatchers("/api/analisis/**").hasAnyAuthority("teacher", "admin")
                        .requestMatchers("/api/admin/**").hasAuthority("admin")
                        .requestMatchers("/api/usuarios/**").hasAuthority("admin")
                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ===========================
    // CORS Configuration - ✅ ÚNICA Y CORRECTA
    // ===========================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ Orígenes permitidos (NO usar "*" con allowCredentials)
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "https://mikhuy-front.web.app",
                "https://mikhuy-front.firebaseapp.com"
        ));

        // ✅ Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // ✅ Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "X-Total-Count"
        ));

        // ✅ Headers expuestos (visibles para el frontend)
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count"
        ));

        // ✅ Permitir credenciales (cookies, headers de auth)
        configuration.setAllowCredentials(true);

        // ✅ Cache de configuración CORS
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}