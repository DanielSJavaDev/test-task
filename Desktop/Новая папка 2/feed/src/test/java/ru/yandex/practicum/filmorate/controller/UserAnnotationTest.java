package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAnnotationTest {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void close() {
        validatorFactory.close();
    }

    @Test
    void addUserTest() {
        User user = User.builder()
                .id(1)
                .email("name@yandex.ru")
                .login("user1")
                .name("Ivan")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void addUserWithEmptyEmailTest() {
        User user = User.builder()
                .id(1)
                .email(" ")
                .login("user1")
                .name("Ivan")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(violations.size(), 1);

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Invalid email format", violation.getMessage());
        assertEquals("email", violation.getPropertyPath().toString());
        assertEquals(" ", violation.getInvalidValue());
    }

    @Test
    void addUserWithInvalidEmailFormatTest() {
        User user = User.builder()
                .id(1)
                .email("@yandex.ru")
                .login("user1")
                .name("Ivan")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(violations.size(), 1);

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Invalid email format", violation.getMessage());
        assertEquals("email", violation.getPropertyPath().toString());
        assertEquals("@yandex.ru", violation.getInvalidValue());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void addUserWithEmptyLoginTest(String incorrectLogin) {
        User user = User.builder()
                .id(1)
                .email("name@yandex.ru")
                .login(incorrectLogin)
                .name("Ivan")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(violations.size(), 1);

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Login must not be empty", violation.getMessage());
        assertEquals("login", violation.getPropertyPath().toString());
    }

    @Test
    void addUserWithBirthdayInTheFutureTest() {
        User user = User.builder()
                .id(1)
                .email("name@yandex.ru")
                .login("Ivan_Ivanov")
                .name("Ivan")
                .birthday(LocalDate.of(3000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(violations.size(), 1);

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Date of birth must not be in the future", violation.getMessage());
        assertEquals("birthday", violation.getPropertyPath().toString());
        assertEquals(LocalDate.of(3000, 1, 1), violation.getInvalidValue());
    }
}
