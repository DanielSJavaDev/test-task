package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface DirectorStorage {
    Collection<Director> getDirectors();
    Director addDirector(Director director);
    Director updateDirector(Director director);
    Director getDirectorById(long directorId);
    boolean removeDirectorById(long id);
    List<Director> getListOfFilmDirectors(long filmId);
}
