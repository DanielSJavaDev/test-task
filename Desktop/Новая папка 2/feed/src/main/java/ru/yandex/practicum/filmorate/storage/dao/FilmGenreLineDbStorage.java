package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreLineStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmGenreLineDbStorage implements FilmGenreLineStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addGenres(List<Genre> genres, long filmId) {
        String sqlQuery = "INSERT INTO FILM_GENRE_LINE (FILM_ID, GENRE_ID) VALUES (?, ?)";
        List<Genre> uniqueGenres = genres.stream()
                .distinct()
                .collect(Collectors.toList());
        getJdbcTemplate().batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                Genre genre = uniqueGenres.get(i);
                ps.setLong(1, filmId);
                ps.setInt(2, genre.getId());
            }
            @Override
            public int getBatchSize() {
                return uniqueGenres.size();
            }
        });
    }

    @Override
    public void deleteGenres(long filmId) {
        String sqlQuery = "delete from FILM_GENRE_LINE where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public List<Integer> getListOfGenres(long id) {
        String sqlQuery = "select GENRE_ID from FILM_GENRE_LINE where FILM_ID = ?";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, id);
    }

    private JdbcOperations getJdbcTemplate() {
        return jdbcTemplate;
    }
}
