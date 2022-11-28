package ru.yandex.practicum.filmorate.storage.inMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.RedoCreationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dal.FilmStorage;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private long id = 0;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        if (films.containsKey(film.getId())) {
            throw new RedoCreationException("Movie already exists");
        }
        if (film.getId() == 0) {
            generateId();
            film.setId(id);
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        filmExistenceCheck(film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(long id) {
        filmExistenceCheck(id);
        return films.get(id);
    }

    @Override //заглушка
    public List<Film> getListOfDirectorFilms(long directorId) {
        return null;
    }

    @Override
    public boolean removeFilmById(long id) {
        return false;
    }
      @Override
    public List<Film> getRecommendations(long userId) {
        return null;
    }

    @Override
    public List<Film> getFilmsByTitleKeyword(String query) {
        return null;
    }

    @Override
    public List<Film> getFilmsByDirectorKeyword(String query) {
        return null;
    }

    @Override
    public List<Film> getFilmsByTitleAndDirectorKeyword(String query) {
        return null;
    }

    private void generateId() {
        id++;
    }

    private void filmExistenceCheck(long id) {
        if (!films.containsKey(id)) {
            throw new ObjectNotFoundException(String.format("Film with id %s not found", id));
        }
    }
}
