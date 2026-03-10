package com.stemlink.skillmentor.configs;

import com.stemlink.skillmentor.constants.UserRoles;
import com.stemlink.skillmentor.security.AuthenticationFilter;
import com.stemlink.skillmentor.security.SkillMentorAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationFilter clerkAuthenticationFilter;
    private final SkillMentorAuthenticationEntryPoint skillMentorAuthenticationEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Apply the CORS configuration from your CorsConfig bean
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // 2. Disable CSRF for stateless REST APIs
                .csrf(AbstractHttpConfigurer::disable)

                // 3. Set Session to Stateless (since we use JWT/Clerk tokens)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Handle Unauthorized Access
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(skillMentorAuthenticationEntryPoint)
                )

                // 5. Request Authorization Rules
                .authorizeHttpRequests(auth -> auth
                        // Public/Swagger Endpoints
                        .requestMatchers(
                                "/api/public/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/swagger-resources/**"
                        ).permitAll()

                        // Public access to view mentors (crucial for home page visitors)
                        // Note: Use "/**" to capture pagination and path variables properly
                        .requestMatchers(HttpMethod.GET, "/api/v1/mentors/**").permitAll()

                        // Admin-only endpoints
                        .requestMatchers("/api/v1/admin/**").hasRole(UserRoles.ROLE_ADMIN)

                        // CRITICAL: Always permit OPTIONS for CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Everything else requires a valid Clerk token
                        .anyRequest().authenticated()
                )

                // 6. Add the Clerk JWT filter before the standard login filter
                .addFilterBefore(clerkAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 7. Disable Basic Auth (not needed for this setup)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Since Clerk handles user management, we don't need a local DB for auth
        return new InMemoryUserDetailsManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}