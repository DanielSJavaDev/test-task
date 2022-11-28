package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.log.Logger;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.dal.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendsStorage friendsStorage;

    @Override
    public Collection<User> getUsers() {
        String sqlQuery = "select * from USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        long userId = simpleJdbcInsert.executeAndReturnKey(toMap(user)).longValue();
        return getUserById(userId);
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update USERS set EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? where USER_ID = ?";
        jdbcTemplate.update(sqlQuery
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , user.getId());
        return getUserById(user.getId());
    }

    @Override
    public User getUserById(long userId) {
        User user;
        String sqlQuery = "select * from USERS where USER_ID = ?";
        try {
            user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
        } catch (DataAccessException e) {
            throw new ObjectNotFoundException(String.format("User with id %s not found", userId));
        }
        return user;
    }

    @Override
    public boolean removeUserById(long id) {
        String sqlQuery = "delete from USERS where USER_ID = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    private Map<String, Object> toMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());
        return values;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(friendsStorage.getListOfFriends(resultSet.getLong("user_id")))
                .build();
    }
}
