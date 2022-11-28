package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class User {
    @PositiveOrZero(message = "id must not be negative")
    private long id;
    @Email(message = "Invalid email format")
    private final String email;
    @NotBlank(message = "Login must not be empty")
    private final String login;
    private String name;
    @PastOrPresent(message = "Date of birth must not be in the future")
    private LocalDate birthday;
    private final List<Long> friends;
}
