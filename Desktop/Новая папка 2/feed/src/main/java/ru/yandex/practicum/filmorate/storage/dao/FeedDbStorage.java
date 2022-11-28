package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.dal.FeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Feed> addFeed(Long userId, String event, String operation, Long entityId) {
        Feed feed = Feed.builder()
                .createTime((long) LocalDateTime.now().getNano())
                .userId(Math.toIntExact(userId))
                .eventType(event)
                .operation(operation)
                .entityId(entityId)
                .build();
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("feed")
                .usingGeneratedKeyColumns("event_id");
        simpleJdbcInsert.executeAndReturnKey(feedToMap(feed)).longValue();
        return getFeedByUserId(feed.getUserId());
    }

    @Override
    public List<Feed> getFeedByUserId(long userId) {
        String sqlQuery = "select * from feed where USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToFeed(rs), userId);
    }

    private Feed mapRowToFeed(ResultSet resultSet) throws SQLException {
        return Feed.builder()
                .eventId(resultSet.getLong("event_id"))
                .createTime(resultSet.getLong("create_time"))
                .userId((int) resultSet.getLong("user_id"))
                .eventType(resultSet.getString("event_type"))
                .operation(resultSet.getString("operation"))
                .entityId(resultSet.getLong("entity_id"))
                .build();
    }

    private Map<String, Object> feedToMap(Feed feed) {
        Map<String, Object> values = new HashMap<>();
        values.put("event_id", feed.getEventId());
        values.put("create_time", feed.getCreateTime());
        values.put("user_id", feed.getUserId());
        values.put("event_type", feed.getEventType());
        values.put("operation", feed.getOperation());
        values.put("entity_id", feed.getEntityId());
        return values;
    }
}
