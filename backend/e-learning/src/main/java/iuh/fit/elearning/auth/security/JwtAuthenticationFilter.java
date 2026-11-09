package iuh.fit.elearning.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.user.entity.User;
import iuh.fit.elearning.user.entity.enums.UserStatus;
import iuh.fit.elearning.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Đọc header "Authorization: Bearer ...", xác thực JWT và nạp người dùng vào SecurityContext.
 * Kiểm tra thêm users.status = ACTIVE để token còn hạn của tài khoản bị khóa cũng bị chặn.
 * <p>
 * Không khai báo @Component: bộ lọc được tạo trong SecurityConfig để chỉ chạy trong chuỗi Spring Security.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Request attribute chứa mã lỗi xác thực để RestAuthenticationEntryPoint trả đúng thông báo. */
    public static final String ERROR_ATTRIBUTE = "auth.error.code";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.regionMatches(true, 0, "Bearer ", 0, 7)) {
            String token = header.substring(7).trim();
            try {
                Claims claims = jwtService.parse(token);
                UUID userId = UUID.fromString(claims.getSubject());
                User user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                    request.setAttribute(ERROR_ATTRIBUTE, ErrorCode.UNAUTHORIZED);
                } else if (user.getStatus() != UserStatus.ACTIVE) {
                    request.setAttribute(ERROR_ATTRIBUTE, ErrorCode.ACCOUNT_LOCKED);
                } else {
                    AuthUser principal = new AuthUser(user.getId(), user.getEmail(), user.getRole());
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            principal, null, List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e) {
                request.setAttribute(ERROR_ATTRIBUTE, ErrorCode.TOKEN_EXPIRED);
            } catch (JwtException | IllegalArgumentException e) {
                request.setAttribute(ERROR_ATTRIBUTE, ErrorCode.UNAUTHORIZED);
            }
        }
        chain.doFilter(request, response);
    }
}
