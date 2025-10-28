package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleAlreadyExistsException(final AlreadyExistsException exception) {
        log.debug("Исключение AlreadyExistsException");

        return new ErrorResponse(
                "Такой объект уже существует",
                exception.getMessage()
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleEntityNotFoundException(final EntityNotFoundException exception) {
        log.debug("Исключение EntityNotFoundException");

        return new ErrorResponse("Объект не найден",
                exception.getMessage()
        );
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleBadRequestException(final BadRequestException exception) {
        log.debug("Исключение BadRequestException");

        return new ErrorResponse("Не корректные данные в запросе",
                exception.getMessage()
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse handleForbiddenException(final ForbiddenException exception) {
        log.debug("Исключение ForbiddenException");

        return new ErrorResponse("Доступ ограничен",
                exception.getMessage()
        );
    }
}
