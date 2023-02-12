package com.kameleoon.test.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kameleoon.test.config.UserControllerTestConfig;
import com.kameleoon.test.config.WebConfig;
import com.kameleoon.test.controller.UserController;
import com.kameleoon.test.dto.UserDto;
import com.kameleoon.test.dto.UserDtoResponse;
import com.kameleoon.test.exception.DuplicateException;
import com.kameleoon.test.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig({ UserController.class, UserControllerTestConfig.class, WebConfig.class})
public class UserControllerTests {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;
    private UserDtoResponse userDtoResponse;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        userDto = UserDto.builder()
                .login("login")
                .email("e@mail.com")
                .build();
        userDtoResponse = UserDtoResponse.builder()
                .login(userDto.getLogin())
                .build();

    }


    @Test
    void createUserTest() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(userDtoResponse);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login", is(userDtoResponse.getLogin())));

        when(userService.createUser(any()))
                .thenThrow(DuplicateException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
}
