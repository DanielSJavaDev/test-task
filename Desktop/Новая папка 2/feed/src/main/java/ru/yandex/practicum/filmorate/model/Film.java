package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class Film {
    @PositiveOrZero(message = "id must not be negative")
    private long id;
    @NotBlank(message = "Movie title must not be empty")
    private final String name;
    @Size(max = 200, message = "Maximum description length - 200 characters")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Movie duration must be positive")
    private int duration;
    private int rate;
    @NotNull(message = "Mpa must not be null")
    private Mpa mpa;
    private final List<Long> likes;
    private final List<Genre> genres;
    private final List<Director> directors;
}
