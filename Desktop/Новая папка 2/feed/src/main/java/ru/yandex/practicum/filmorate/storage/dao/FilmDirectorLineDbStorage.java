package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dal.FilmDirectorLineStorage;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmDirectorLineDbStorage implements FilmDirectorLineStorage {
    private final JdbcTemplate jdbcTemplate;

    public void addDirectors(List<Director> directors, long filmId) {
        LinkedList<Long> params = new LinkedList<>(directors.stream()
                .map(Director::getId)
                .distinct()
                .collect(Collectors.toList()));
        String inSql = String.join(",", Collections.nCopies(params.size(), "?"));
        String insertSql = String.format("INSERT INTO film_director_line(film_id, director_id) " +
                "SELECT ?, directors.director_id FROM directors WHERE directors.director_id IN (%s)", inSql);
        params.addFirst(filmId);
        jdbcTemplate.update(insertSql, params.toArray());
    }

    @Override
    public void deleteDirectors(long filmId) {
        String sqlQuery = "delete from film_director_line where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }
}
