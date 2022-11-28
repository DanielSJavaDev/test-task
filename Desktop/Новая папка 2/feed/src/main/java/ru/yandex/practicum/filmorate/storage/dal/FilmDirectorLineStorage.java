package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface FilmDirectorLineStorage {
    void addDirectors(List<Director> directors, long filmId);
    void deleteDirectors(long filmId);
}
