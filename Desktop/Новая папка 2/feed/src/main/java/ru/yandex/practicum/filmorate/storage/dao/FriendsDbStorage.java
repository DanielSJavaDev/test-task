package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.storage.dal.FriendsStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean addAsFriend(long userId, long friendId) {
        Friends friends = Friends.builder()
                .userId(userId)
                .friendId(friendId)
                .build();
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friends");
        return simpleJdbcInsert.execute(toMap(friends)) > 0;
    }

    @Override
    public boolean removeFromFriends(long userId, long friendId) {
        String sqlQuery = "delete from FRIENDS where USER_ID = ? and FRIEND_ID = ?";
        return jdbcTemplate.update(sqlQuery, userId, friendId) > 0;
    }

    @Override
    public List<Long> getListOfFriends(long userId) {
        String sqlQuery = "select FRIEND_ID from FRIENDS where USER_ID = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, userId);
    }

    @Override
    public List<Long> getAListOfMutualFriends(long userId, long otherId) {
        String sqlQuery = "select FRIEND_ID " +
                "from (select *  from FRIENDS where USER_ID = ? or USER_ID = ?) " +
                "group by FRIEND_ID HAVING (COUNT(*) > 1)";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, userId, otherId);
    }

    private Map<String, Object> toMap(Friends friends) {
        Map<String, Object> values = new HashMap<>();
        values.put("user_Id", friends.getUserId());
        values.put("friend_Id", friends.getFriendId());
        return values;
    }
}
