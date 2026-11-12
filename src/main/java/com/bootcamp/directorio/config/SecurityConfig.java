package com.bootcamp.directorio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // API sin estado: cada peticion trae su cabecera Authorization: Basic ...
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Archivos estaticos: la pagina de login y sus recursos deben ser publicos
                .requestMatchers("/", "/index.html", "/login.html", "/css/**", "/js/**", "/favicon.ico").permitAll()
                // Toda la API requiere credenciales
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated())
            .httpBasic(basic -> basic.authenticationEntryPoint(entryPoint401()));
        return http.build();
    }

    /**
     * Responde 401 en JSON sin la cabecera WWW-Authenticate, para que el navegador
     * NO abra su ventana emergente y se use nuestro formulario de login.
     */
    private AuthenticationEntryPoint entryPoint401() {
        return (request, response, ex) -> {
            response.setStatus(401);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("{\"message\":\"Usuario o contraseña incorrectos.\"}");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder,
                                                 @Value("${app.security.user}") String usuario,
                                                 @Value("${app.security.password}") String clave) {
        return new InMemoryUserDetailsManager(
                User.withUsername(usuario)
                    .password(encoder.encode(clave))
                    .roles("ADMIN")
                    .build());
    }
}
