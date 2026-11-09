package iuh.fit.elearning.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Cấu trúc phản hồi thống nhất cho mọi API (SDS mục 6.2).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data,
        List<FieldErrorItem> errors,
        Instant timestamp
) {

    public record FieldErrorItem(String field, String message) {
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "OK", "Thành công", data, null, Instant.now());
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, "OK", message, data, null, Instant.now());
    }

    public static ApiResponse<Void> message(String message) {
        return new ApiResponse<>(true, "OK", message, null, null, Instant.now());
    }

    public static ApiResponse<Void> error(String code, String message) {
        return new ApiResponse<>(false, code, message, null, null, Instant.now());
    }

    public static ApiResponse<Void> error(String code, String message, List<FieldErrorItem> errors) {
        return new ApiResponse<>(false, code, message, null, errors, Instant.now());
    }
}
