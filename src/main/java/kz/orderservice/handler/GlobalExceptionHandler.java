package kz.orderservice.handler;

import jakarta.persistence.EntityNotFoundException;
import kz.orderservice.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> errorResponseMap = new HashMap<>();
        exception.getFieldErrors()
                .forEach(error -> errorResponseMap.put(error.getField(), error.getDefaultMessage()));
        return errorResponseMap;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleEntityNotFoundException(EntityNotFoundException exception) {
        log.error(exception.getMessage());
        return ErrorResponseDto
                .builder()
                .error("Entity Not Found")
                .errorMessage(exception.getMessage())
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error(exception.getMessage());
        return ErrorResponseDto
                .builder()
                .error("Illegal Argument")
                .errorMessage(exception.getMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleHttpMessageNotReadableException(Exception exception) {
        log.error(exception.getMessage());
        return ErrorResponseDto
                .builder()
                .error("Internal Server Error")
                .errorMessage(exception.getMessage())
                .build();
    }
}
