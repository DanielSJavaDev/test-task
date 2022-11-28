package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmAnnotationTest {
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
    void addFilmTest() {
        Film film = Film.builder()
                .id(1)
                .name("Psycho")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .mpa(Mpa.builder().id(1).name("G").build())
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void addFilmWithIncorrectNameTest(String incorrectName) {
        Film film = Film.builder()
                .id(1)
                .name(incorrectName)
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .mpa(Mpa.builder().id(1).name("G").build())
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(violations.size(), 1);

        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Movie title must not be empty", violation.getMessage());
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    void addAFilmWithALongDescriptionTest() {
        Film film = Film.builder()
                .id(1)
                .name("Psycho")
                .description("американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком " +
                        "по сценарию Джозефа Стефано, основанном на одноимённом романе Роберта Блоха. Главные " +
                        "роли в картине исполняют Энтони Перкинс, Джанет Ли, Вера Майлз, Джон Гэвин, Мартин " +
                        "Болсам и Джон Макинтайр.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .mpa(Mpa.builder().id(1).name("G").build())
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(violations.size(), 1);

        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Maximum description length - 200 characters", violation.getMessage());
        assertEquals("description", violation.getPropertyPath().toString());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void addFilmWithIncorrectDurationTest(int incorrectDuration) {
        Film film = Film.builder()
                .id(1)
                .name("Psycho")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(incorrectDuration)
                .mpa(Mpa.builder().id(1).name("G").build())
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(violations.size(), 1);

        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Movie duration must be positive", violation.getMessage());
        assertEquals("duration", violation.getPropertyPath().toString());
    }

    @ParameterizedTest
    @NullSource
    void addFilmWithNullMpaTest(Mpa nullMpa) {
        Film film = Film.builder()
                .id(1)
                .name("Psycho")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .mpa(nullMpa)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(violations.size(), 1);

        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Mpa must not be null", violation.getMessage());
        assertEquals("mpa", violation.getPropertyPath().toString());
    }
}
