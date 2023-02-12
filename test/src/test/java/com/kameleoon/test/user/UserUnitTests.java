package com.kameleoon.test.user;

import com.kameleoon.test.dto.UserDto;
import com.kameleoon.test.exception.DuplicateException;
import com.kameleoon.test.model.User;
import com.kameleoon.test.repositories.UserRepository;
import com.kameleoon.test.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserUnitTests {

    private UserServiceImpl userService;
    private UserDto userDto;
    private User user;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .login("login")
                .email("e@mail.com")
                .build();
        userService = new UserServiceImpl(userRepository);
        userDto = UserDto.builder()
                .email(user.getEmail())
                .login(user.getLogin())
                .build();
    }

    @Test
    void creteUserTestException() {
        Mockito
                .when(userRepository.save(any()))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(DuplicateException.class, () -> userService.createUser(userDto));

    }

    @Test
    void createUserTest() {
        Mockito
                .when(userRepository.save(any()))
                .thenReturn(user);

        assertThat(userService.createUser(userDto).getLogin().equals("login"));
    }
}
