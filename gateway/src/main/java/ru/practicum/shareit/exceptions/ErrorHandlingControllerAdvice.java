package ru.practicum.shareit.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onConstraintValidationException(final ConstraintViolationException exception) {
        final ValidationErrorResponse error = new ValidationErrorResponse();
        error.setViolations(
                exception.getConstraintViolations()
                        .stream()
                        .map(violation -> new Violation(violation.getPropertyPath().toString(), violation.getMessage()))
                        .toList()
        );
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        final ValidationErrorResponse error = new ValidationErrorResponse();
        exception.getBindingResult().getFieldErrors()
                .forEach(fieldError -> error.getViolations().add(new Violation(fieldError.getField(), fieldError.getDefaultMessage())));
        return error;
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException exception) {
        log.debug("Исключение BadRequestException");
        return new ErrorResponse("Некорректные данные в запросе",
                exception.getMessage()
        );
    }
}
