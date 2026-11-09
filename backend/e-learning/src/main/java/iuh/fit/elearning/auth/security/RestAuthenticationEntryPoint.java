package iuh.fit.elearning.auth.security;

import iuh.fit.elearning.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** 401 khi thiếu token / token sai / hết hạn (kèm mã TOKEN_EXPIRED để app gọi /auth/refresh). */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        Object attr = request.getAttribute(JwtAuthenticationFilter.ERROR_ATTRIBUTE);
        ErrorCode code = attr instanceof ErrorCode c ? c : ErrorCode.UNAUTHORIZED;
        JsonErrorWriter.write(response, code, code.getDefaultMessage());
    }
}
