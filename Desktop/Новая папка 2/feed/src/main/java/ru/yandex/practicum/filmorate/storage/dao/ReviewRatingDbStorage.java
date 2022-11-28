package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ReviewRating;
import ru.yandex.practicum.filmorate.storage.dal.ReviewRatingStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class ReviewRatingDbStorage implements ReviewRatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean addLikeDislike(long reviewId, long userId, boolean isUseful) {
        ReviewRating reviewRating = ReviewRating.builder()
                .reviewId(reviewId)
                .userId(userId)
                .isUseful(isUseful)
                .build();
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("review_rating");
        return simpleJdbcInsert.execute(toMap(reviewRating)) > 0;
    }

    @Override
    public boolean removeLikeDislike(long reviewId, long userId, boolean isUseful) {
        String sqlQuery = "delete from REVIEW_RATING where REVIEW_ID = ? and USER_ID = ? and IS_USEFUL = ?";
        return jdbcTemplate.update(sqlQuery, reviewId, userId, isUseful) > 0;
    }

    @Override
    public long getReviewRating(long reviewId) {
        String sqlQuery = "select IS_USEFUL, COUNT(REVIEW_ID) AS RATING " +
                "from REVIEW_RATING " +
                "where REVIEW_ID = ? " +
                "group by IS_USEFUL " +
                "order by IS_USEFUL desc";
        List<Map<String,Object>> mapListRating = jdbcTemplate.queryForList(sqlQuery, reviewId);
        if(mapListRating.size() == 2) {
            return (Long) mapListRating.get(0).get("RATING") - (Long) mapListRating.get(1).get("RATING");
        } else if (mapListRating.size() == 1) {
            if (mapListRating.get(0).containsValue(TRUE)) {
                return (Long) mapListRating.get(0).get("RATING");
            } else if (mapListRating.get(0).containsValue(FALSE)) {
                return - ((Long) mapListRating.get(0).get("RATING"));
            }
        }
            return 0;
    }

    private Map<String, Object> toMap(ReviewRating reviewRating) {
        Map<String, Object> values = new HashMap<>();
        values.put("review_id", reviewRating.getReviewId());
        values.put("user_id", reviewRating.getUserId());
        values.put("is_useful", reviewRating.getIsUseful());
        return values;
    }
}
