package com.kameleoon.test.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@RequiredArgsConstructor
public class QuoteNotFoundException extends RuntimeException {
    private final String parameter;

    public String getParameter() {
        return parameter;
    }
}
