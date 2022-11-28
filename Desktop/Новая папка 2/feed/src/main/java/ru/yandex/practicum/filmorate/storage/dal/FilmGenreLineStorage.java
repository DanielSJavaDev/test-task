package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreLineStorage {
    void addGenres(List<Genre> genres, long filmId);
    void deleteGenres(long filmId);
    List<Integer> getListOfGenres(long id);

}
