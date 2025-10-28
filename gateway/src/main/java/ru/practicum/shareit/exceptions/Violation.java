package ru.practicum.shareit.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Violation {

    @Getter
    private final String error;
    @Getter
    private final String message;

    public Violation(String fieldName, String message) {
        this.error = fieldName;
        this.message = message;
    }
}
