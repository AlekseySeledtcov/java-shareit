package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleAlreadyExistsException(final AlreadyExistsException exception) {
        log.debug("Исключение AlreadyExistsException");

        Map<String, String> description = new HashMap<>();
        description.put("message: ", exception.getMessage());

        return new ErrorResponse(
                "Такой объект уже существует",
                description
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleEntityNotFoundException(final EntityNotFoundException exception) {
        log.debug("Исключение EntityNotFoundException");

        Map<String, String> description = new HashMap<>();
        description.put("message: ", exception.getMessage());

        return new ErrorResponse("Объект не найден",
                description
        );
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleBadRequestException(final BadRequestException exception) {
        log.debug("Исключение BadRequestException");

        Map<String, String> description = new HashMap<>();
        description.put("message: ", exception.getMessage());

        return new ErrorResponse("Не корректные данные в запросе",
                description
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse handleForbiddenException(final ForbiddenException exception) {
        log.debug("Исключение ForbiddenException");

        Map<String, String> description = new HashMap<>();
        description.put("message: ", exception.getMessage());

        return new ErrorResponse("Доступ ограничен",
                description
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse onMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        Map<String, String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage
                ));

        return new ErrorResponse(
                "Ошибка валидации",
                errors
        );
    }
}
