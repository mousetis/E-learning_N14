package iuh.fit.elearning.auth.security;

import iuh.fit.elearning.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Ghi phản hồi lỗi JSON (cùng định dạng ApiResponse) từ tầng Security, nơi GlobalExceptionHandler không tới được.
 */
final class JsonErrorWriter {

    private JsonErrorWriter() {
    }

    static void write(HttpServletResponse response, ErrorCode code, String message) throws IOException {
        response.setStatus(code.getStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");
        String json = "{\"success\":false,\"code\":\"" + code.name() + "\",\"message\":\""
                + message.replace("\\", "\\\\").replace("\"", "\\\"")
                + "\",\"timestamp\":\"" + Instant.now() + "\"}";
        response.getWriter().write(json);
    }
}
