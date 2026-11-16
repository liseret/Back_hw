package app.config;

import app.ApiException;
import app.domain.exception.ConflictException;
import app.domain.exception.NotFoundException;
import app.domain.exception.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(NotFoundException e) {
        return resp(404, error("NOT_FOUND", e.code(), e.getMessage()));
    }
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> conflict(ConflictException e) {
        return resp(409, error("CONFLICT", e.code(), e.getMessage()));
    }
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> validation(ValidationException e) {
        return resp(400, error("BAD_REQUEST", e.code(), e.getMessage()));
    }
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> api(ApiException e) {
        Map<String, Object> b = new LinkedHashMap<>();
        b.put("error", e.code);
        b.put("message", e.getMessage());
        return ResponseEntity.status(e.status).body(b);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> beanValidation(MethodArgumentNotValidException e) {
        String field = e.getBindingResult().getFieldErrors().stream().map(FieldError::getField).findFirst().orElse("field");
        return resp(400, error("MISSING_FIELD", null, "Поле " + field + " обязательно и должно быть строкой"));
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> noRoute(NoResourceFoundException e) {
        return resp(404, error("NOT_FOUND", null, "Такого путя нет"));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> badBody(HttpMessageNotReadableException e) {
        String m = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
        if (m.contains("required request body is missing"))
            return resp(400, error("EMPTY_BODY", null, "Тело запроса обязательно"));
        return resp(400, error("INVALID_JSON", null, "Некорректный JSON"));
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> method(HttpRequestMethodNotSupportedException e) {
        String allow = e.getSupportedMethods() == null ? "" : String.join(", ", e.getSupportedMethods());
        return ResponseEntity.status(405).header("Allow", allow).body(error("METHOD_NOT_ALLOWED", null, "Метод не поддерживается. Допустимые методы: " + allow));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> generic(Exception e) {
        e.printStackTrace();
        return resp(500, error("INTERNAL_ERROR", null, "Внутренняя ошибка сервера"));
    }

    private ResponseEntity<Map<String, Object>> resp(int status, Map<String, Object> body) {
        return ResponseEntity.status(status).body(body);
    }
    private Map<String, Object> error(String err, String code, String msg) {
        Map<String, Object> b = new LinkedHashMap<>();
        b.put("error", err);
        if (code != null) b.put("code", code);
        b.put("message", msg);
        return b;
    }
}