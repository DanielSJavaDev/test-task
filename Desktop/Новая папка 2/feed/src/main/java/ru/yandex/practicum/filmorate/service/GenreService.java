package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.log.Logger;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreLineStorage;
import ru.yandex.practicum.filmorate.storage.dal.GenreStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;
    private final FilmGenreLineStorage filmGenreLineStorage;

    public Collection<Genre> getGenres() {
        Collection<Genre> genreInStorage = genreStorage.getGenres();
        Logger.logSave(HttpMethod.GET, "/genres", genreInStorage.toString());
        return genreInStorage;
    }

    public Genre getGenreById(int id) {
        Genre genreInStorage = genreStorage.getGenreById(id);
        Logger.logSave(HttpMethod.GET, "/genres/" + id, genreInStorage.toString());
        return genreInStorage;
    }

    public List<Genre> getListOfGenres(long id) {
        List<Genre> genreList = filmGenreLineStorage.getListOfGenres(id).stream()
                .map(genreStorage::getGenreById)
                .collect(Collectors.toList());
        return genreList;
    }
}
