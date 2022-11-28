package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dal.ReviewRatingStorage;
import ru.yandex.practicum.filmorate.storage.dal.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewRatingStorage reviewRatingStorage;

    @Override
    public Review addReview(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        long reviewId = simpleJdbcInsert.executeAndReturnKey(toMap(review)).longValue();
        return getReviewById(reviewId);
    }

    @Override
    public Review updateReview(Review review) {
        String sqlQuery = "update REVIEWS set CONTENT = ?, IS_POSITIVE = ? where REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery
                , review.getContent()
                , review.getIsPositive()
                , review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public boolean removeReview(long reviewId) {
        String sqlQuery = "delete from REVIEWS where REVIEW_ID = ?";
        return jdbcTemplate.update(sqlQuery, reviewId) > 0;
    }

    @Override
    public Review getReviewById(long reviewId) {
        Review review;
        String sqlQuery = "select * from REVIEWS where REVIEW_ID = ?";
        try {
            review = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, reviewId);
        } catch (DataAccessException e) {
            throw new ObjectNotFoundException(String.format("Review with id %s not found", reviewId));
        }
        return review;
    }

    @Override
    public List<Review> getReviews(long filmId, int count) {
        if (filmId == 0) {
            String sqlQuery = "select * from REVIEWS";
            return jdbcTemplate.query(sqlQuery, this::mapRowToReview);
        } else {
            String sqlQuery = "select * from REVIEWS where FILM_ID = ? limit + ?";
            return jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, count);
        }
    }

    private Map<String, Object> toMap(Review review) {
        Map<String, Object> values = new HashMap<>();
        values.put("content", review.getContent());
        values.put("is_positive", review.getIsPositive());
        values.put("user_id", review.getUserId());
        values.put("film_id", review.getFilmId());
        return values;
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("review_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getLong("user_id"))
                .filmId(resultSet.getLong("film_id"))
                .useful(reviewRatingStorage.getReviewRating(resultSet.getLong("review_id")))
                .build();
    }
}
