package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review addReview(Review review);
    Review updateReview(Review review);
    boolean removeReview(long id);
    Review getReviewById(long id);
    List<Review> getReviews(long filmId, int count);
}