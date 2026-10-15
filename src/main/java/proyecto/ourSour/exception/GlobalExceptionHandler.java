package proyecto.ourSour.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
 
    public record ErrorResponse(int status, String message,
                                Map<String, String> fieldErrors, LocalDateTime timestamp) {
    }
 
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApi(ApiException ex) {
        ErrorResponse body = new ErrorResponse(ex.getStatus().value(), ex.getMessage(),
                null, LocalDateTime.now());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }
 
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(f -> errors.putIfAbsent(f.getField(), f.getDefaultMessage()));
        ErrorResponse body = new ErrorResponse(400, "Datos inválidos", errors, LocalDateTime.now());
        return ResponseEntity.badRequest().body(body);
    }
}

