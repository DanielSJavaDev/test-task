package com.kameleoon.test.controller;

import com.kameleoon.test.dto.UserDto;
import com.kameleoon.test.dto.UserDtoResponse;
import com.kameleoon.test.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public UserDtoResponse createUser(@Valid @RequestBody UserDto user) {
        return service.createUser(user);
    }
}
