package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dal.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Director> getDirectors() {
        String sqlQuery = "select * from directors";
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    @Override
    public List<Director> getListOfFilmDirectors(long filmId) {
        String sqlQuery =  "SELECT directors.* FROM directors INNER JOIN film_director_line " +
                "ON directors.director_id = film_director_line.director_id " +
                "WHERE film_director_line.film_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector, filmId);
    }

    @Override
    public Director addDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        long directorId = simpleJdbcInsert.executeAndReturnKey(toMap(director)).longValue();
        return getDirectorById(directorId);
    }

    @Override
    public Director updateDirector(Director director) {
        String sqlQuery = "update DIRECTORS set NAME = ? where DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQuery
                , director.getName()
                , director.getId());
        return getDirectorById(director.getId());
    }

    @Override
    public Director getDirectorById(long directorId) {
        Director director;
        String sqlQuery = "select * from DIRECTORS where DIRECTOR_ID = ?";
        try {
            director = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToDirector, directorId);
        } catch (DataAccessException e) {
            throw new ObjectNotFoundException(String.format("Director with id %s not found", directorId));
        }
        return director;
    }

    @Override
    public boolean removeDirectorById(long id) {
        String sqlQuery = "delete from DIRECTORS where DIRECTOR_ID = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getLong("director_id"))
                .name(resultSet.getString("name"))
                .build();
    }

    private Map<String, Object> toMap(Director director) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", director.getName());
        return values;
    }
}
