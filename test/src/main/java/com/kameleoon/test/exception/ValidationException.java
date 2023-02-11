package com.kameleoon.test.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@RequiredArgsConstructor
public class ValidationException extends RuntimeException {

    private final String parameter;

    public String getParameter() {
        return parameter;
    }
}
