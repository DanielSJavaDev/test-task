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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.dal.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final LikesStorage likesStorage;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final FilmGenreLineStorage filmGenreLineStorage;
    private final FilmDirectorLineStorage filmDirectorLineStorage;
    private final DirectorStorage directorStorage;

    @Override
    public Collection<Film> getFilms() {
        String sqlQuery = "select * from FILMS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        long filmId = simpleJdbcInsert.executeAndReturnKey(toMap(film)).longValue();

        //Добавить жанры
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            filmGenreLineStorage.addGenres(film.getGenres(), filmId);
        }

        //Добавить режисеров
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            filmDirectorLineStorage.addDirectors(film.getDirectors(), filmId);
        }
        return getFilmById(filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "update FILMS set NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATE = ?, " +
                "MPA_ID = ? where FILM_ID = ?";

        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getRate()
                , film.getMpa().getId()
                , film.getId());

        Film oldFilm = getFilmById(film.getId());

        //Очистить жанры
        Collection<Genre> existingGenres = oldFilm.getGenres();
        if (existingGenres != null && !existingGenres.isEmpty()) {
            filmGenreLineStorage.deleteGenres(film.getId());
        }
        //Добавить жанры
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            filmGenreLineStorage.addGenres(film.getGenres(), film.getId());
        }

        //Очистить режиссеров
        Collection<Director> existingDirectors = oldFilm.getDirectors();
        if (existingDirectors != null && !existingDirectors.isEmpty()) {
            filmDirectorLineStorage.deleteDirectors(film.getId());
        }
        //Добавить режиссеров
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            filmDirectorLineStorage.addDirectors(film.getDirectors(), film.getId());
        }
        return getFilmById(film.getId());
    }

    @Override
    public Film getFilmById(long filmId) {
        Film film;
        String sqlQuery = "select * from FILMS where FILM_ID = ?";
        try {
            film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
        } catch (DataAccessException e) {
            throw new ObjectNotFoundException(String.format("Film with id %s not found", filmId));
        }
        return film;
    }

    @Override
    public List<Film> getListOfDirectorFilms(long directorId) {
        String sqlQuery = "SELECT films.* FROM films INNER JOIN film_director_line " +
                "ON films.film_id = film_director_line.film_id " +
                "WHERE film_director_line.director_id = ?";
        directorStorage.getDirectorById(directorId);
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, directorId);
    }

    @Override
    public boolean removeFilmById(long id) {
        String sqlQuery = "delete from FILMS where FILM_ID = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public List<Film> getRecommendations(long userId) {
        String sqlQuery = "select * from FILMS right join LIKES ON FILMS.FILM_ID=LIKES.FILM_ID " +
                "where LIKES.USER_ID IN (select USER_ID from LIKES where NOT USER_ID = ? AND FILM_ID IN " +
                "(select FILM_ID from LIKES where USER_ID=?) " +
                "group by USER_ID order by COUNT(FILM_ID) desc LIMIT 1) " +
                "and not LIKES.FILM_ID IN (select FILM_ID from LIKES where USER_ID=?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId, userId, userId);
    }

    @Override
    public List<Film> getFilmsByTitleKeyword(String lowerCaseQuery) {
        String sqlQuery =
                "SELECT films.* FROM films " +
                        "LEFT JOIN likes " +
                        "ON films.film_id = likes.film_id " +
                        "WHERE LOWER(films.name) LIKE '%'||?||'%' " +
                        "GROUP BY films.film_id " +
                        "ORDER BY COUNT(likes.user_id) DESC";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, lowerCaseQuery);
    }

    @Override
    public List<Film> getFilmsByDirectorKeyword(String lowerCaseQuery) {
        String sqlQuery = "SELECT films.* FROM films " +
                "LEFT JOIN likes " +
                "ON films.film_id = likes.film_id " +
                "JOIN film_director_line " +
                "ON films.film_id = film_director_line.film_id " +
                "JOIN directors " +
                "ON film_director_line.director_id = directors.director_id " +
                "WHERE LOWER(directors.name) LIKE '%'||?||'%' " +
                "GROUP BY films.film_id " +
                "ORDER BY COUNT(likes.user_id) DESC";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, lowerCaseQuery);
    }

    @Override
    public List<Film> getFilmsByTitleAndDirectorKeyword(String lowerCaseQuery) {
            String sqlQuery =
                    "SELECT films.* FROM films " +
                            "LEFT JOIN likes " +
                            "ON films.film_id = likes.film_id " +
                            "WHERE LOWER(films.name) LIKE '%'||?||'%' OR films.film_id IN " +
                                "(SELECT film_director_line.film_id FROM film_director_line " +
                                "JOIN directors " +
                                "ON film_director_line.director_id = directors.director_id " +
                                "WHERE LOWER(directors.name) LIKE '%'||?||'%') " +
                            "GROUP BY films.film_id " +
                            "ORDER BY COUNT(likes.user_id) DESC";
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, lowerCaseQuery, lowerCaseQuery);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .rate(resultSet.getInt("rate"))
                .mpa(mpaService.getMpaById(resultSet.getInt("mpa_id")))
                .likes(likesStorage.getListOfLikes(resultSet.getLong("film_id")))
                .genres(genreService.getListOfGenres(resultSet.getLong("film_id")))
                .directors(directorStorage.getListOfFilmDirectors(resultSet.getLong("film_id")))
                .build();
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("rate", film.getRate());
        values.put("MPA_id", film.getMpa().getId());
        return values;
    }
}