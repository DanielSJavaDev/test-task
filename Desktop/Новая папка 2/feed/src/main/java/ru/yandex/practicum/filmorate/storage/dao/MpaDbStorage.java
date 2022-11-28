package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dal.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> getMpa() {
        String sqlQuery = "select * from RATING_MPA";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public Mpa getMpaById(int mpaId) {
        Mpa mpa;
        String sqlQuery = "select * from RATING_MPA where MPA_ID = ?";
        try {
            mpa = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, mpaId);
        } catch (DataAccessException e) {
            throw new ObjectNotFoundException(String.format("MPA with id %s not found", mpaId));
        }
        return mpa;
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("MPA_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
