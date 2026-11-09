package iuh.fit.elearning.common.exception;

import iuh.fit.elearning.common.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

/**
 * Chuyển mọi ngoại lệ thành ApiResponse lỗi thống nhất (SDS mục 5.5).
 * <p>
 * Kế thừa ResponseEntityExceptionHandler để các lỗi chuẩn của Spring MVC
 * (thiếu tham số, sai kiểu, sai method, 404, 415...) cũng được trả về đúng định dạng ApiResponse.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        ErrorCode code = ex.getErrorCode();
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.error(code.name(), ex.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraint(ConstraintViolationException ex) {
        List<ApiResponse.FieldErrorItem> errors = ex.getConstraintViolations().stream()
                .map(v -> new ApiResponse.FieldErrorItem(String.valueOf(v.getPropertyPath()), v.getMessage()))
                .toList();
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR.name(),
                        ErrorCode.VALIDATION_ERROR.getDefaultMessage(), errors));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ErrorCode.FORBIDDEN.name(), ErrorCode.FORBIDDEN.getDefaultMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleIntegrity(DataIntegrityViolationException ex) {
        log.warn("Vi phạm ràng buộc dữ liệu: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ErrorCode.DUPLICATE.name(), ErrorCode.DUPLICATE.getDefaultMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleOther(Exception ex) {
        if (ex instanceof MaxUploadSizeExceededException) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(ApiResponse.error(ErrorCode.FILE_TOO_LARGE.name(), ErrorCode.FILE_TOO_LARGE.getDefaultMessage()));
        }
        log.error("Lỗi không mong muốn", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.INTERNAL_ERROR.name(), ErrorCode.INTERNAL_ERROR.getDefaultMessage()));
    }

    /** Lỗi @Valid trên request body: kèm danh sách trường lỗi. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<ApiResponse.FieldErrorItem> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> new ApiResponse.FieldErrorItem(f.getField(), f.getDefaultMessage()))
                .toList();
        ApiResponse<Void> body = ApiResponse.error(ErrorCode.VALIDATION_ERROR.name(),
                ErrorCode.VALIDATION_ERROR.getDefaultMessage(), errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /** Mọi lỗi chuẩn còn lại của Spring MVC: giữ nguyên HTTP status, đổi body sang ApiResponse. */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        String code;
        String message;
        int value = statusCode.value();
        if (value == 404) {
            code = ErrorCode.RESOURCE_NOT_FOUND.name();
            message = ErrorCode.RESOURCE_NOT_FOUND.getDefaultMessage();
        } else if (value == 405) {
            code = ErrorCode.METHOD_NOT_ALLOWED.name();
            message = ErrorCode.METHOD_NOT_ALLOWED.getDefaultMessage();
        } else if (value == 413) {
            code = ErrorCode.FILE_TOO_LARGE.name();
            message = ErrorCode.FILE_TOO_LARGE.getDefaultMessage();
        } else if (value == 415) {
            code = ErrorCode.UNSUPPORTED_FILE_TYPE.name();
            message = ErrorCode.UNSUPPORTED_FILE_TYPE.getDefaultMessage();
        } else if (value >= 500) {
            log.error("Lỗi máy chủ", ex);
            code = ErrorCode.INTERNAL_ERROR.name();
            message = ErrorCode.INTERNAL_ERROR.getDefaultMessage();
        } else {
            code = ErrorCode.BAD_REQUEST.name();
            message = "Yêu cầu không hợp lệ: thiếu hoặc sai định dạng tham số / nội dung";
        }
        return ResponseEntity.status(statusCode).headers(headers).body(ApiResponse.error(code, message));
    }
}
