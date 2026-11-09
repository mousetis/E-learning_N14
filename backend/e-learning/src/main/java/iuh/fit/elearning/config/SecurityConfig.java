package iuh.fit.elearning.config;

import iuh.fit.elearning.auth.security.JwtAuthenticationFilter;
import iuh.fit.elearning.auth.security.JwtService;
import iuh.fit.elearning.auth.security.RestAccessDeniedHandler;
import iuh.fit.elearning.auth.security.RestAuthenticationEntryPoint;
import iuh.fit.elearning.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Cấu hình bảo mật (SDS mục 8): JWT stateless, phân quyền theo đường dẫn.
 * Lớp phân quyền thứ hai (chủ sở hữu, học viên đã ghi danh, giảng viên của khóa) nằm trong Service.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtService jwtService,
                                                   UserRepository userRepository,
                                                   RestAuthenticationEntryPoint entryPoint,
                                                   RestAccessDeniedHandler accessDeniedHandler,
                                                   CorsConfigurationSource corsConfigurationSource) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtService, userRepository);

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Khu vực theo vai trò
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/instructor/**").hasAnyRole("INSTRUCTOR", "ADMIN")

                        // Public: xác thực
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/register",
                                "/api/v1/auth/verify-otp",
                                "/api/v1/auth/resend-otp",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/reset-password").permitAll()

                        // Public: xem nội dung
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/categories",
                                "/api/v1/categories/*/courses",
                                "/api/v1/topics/hot",
                                "/api/v1/courses",
                                "/api/v1/courses/popular",
                                "/api/v1/courses/for-you",
                                "/api/v1/courses/*",
                                "/api/v1/courses/*/curriculum",
                                "/api/v1/courses/*/reviews",
                                "/api/v1/courses/*/similar",
                                "/api/v1/courses/*/qa-posts",
                                "/api/v1/lessons/*/preview",
                                "/api/v1/banners",
                                "/api/v1/instructors",
                                "/api/v1/instructors/top",
                                "/api/v1/instructors/*",
                                "/api/v1/instructors/*/courses",
                                "/api/v1/instructors/*/reviews",
                                "/api/v1/qa-posts/*/comments",
                                "/api/v1/certificates/*",
                                "/api/v1/certificates/*/pdf",
                                "/api/v1/files/public/**",
                                "/api/v1/files/content/**").permitAll()

                        // Public: cổng thanh toán (mock, VNPay return/IPN) - xác thực bằng chữ ký
                        .requestMatchers("/api/v1/payments/**").permitAll()

                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:*}") String allowedOrigins) {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(",")).map(String::trim)
                .filter(s -> !s.isEmpty()).toList();
        config.setAllowedOriginPatterns(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
