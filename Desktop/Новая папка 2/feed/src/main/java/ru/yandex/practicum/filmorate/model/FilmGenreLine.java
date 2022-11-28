package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmGenreLine {
    private long filmId;
    private long genreId;
}
