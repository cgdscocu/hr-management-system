package com.hrapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 🔒 Security Configuration - Güvenlik Ayarları
 * 
 * Spring Security konfigürasyonu
 * - Password encoding
 * - HTTP Security
 * - API endpoints güvenlik kuralları
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 🔐 Password Encoder Bean
     * 
     * BCrypt algoritması kullanarak şifreleri hash'ler
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // 12 round - güvenli seviye
    }

    /**
     * 🛡️ Security Filter Chain
     * 
     * HTTP güvenlik kurallarını tanımlar
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF korumasını devre dışı bırak (REST API için)
                .csrf(csrf -> csrf.disable())
                
                // CORS ayarları (ileride frontend için gerekli)
                .cors(cors -> cors.disable())
                
                // Session yönetimi - Stateless (JWT için)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Authorization kuralları
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - herkes erişebilir
                        .requestMatchers("/health/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/register").permitAll()
                        
                        // Admin endpoints - sadece ADMIN ve SUPER_ADMIN
                        .requestMatchers("/users/*/roles/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/roles/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/tenants/**").hasRole("SUPER_ADMIN")
                        .requestMatchers("/dimensions/**").hasAnyRole("ADMIN", "HR_MANAGER", "SUPER_ADMIN")
                        .requestMatchers("/success-profiles/**").hasAnyRole("ADMIN", "HR_MANAGER", "SUPER_ADMIN")
                        .requestMatchers("/surveys/**").hasAnyRole("ADMIN", "HR_MANAGER", "SUPER_ADMIN")
                        
                        // Diğer tüm endpoints - kimlik doğrulaması gerekli
                        .anyRequest().authenticated())
                
                // H2 Console için frame options (development için)
                .headers(headers -> headers
                        .frameOptions().sameOrigin())
                
                .build();
    }
} 