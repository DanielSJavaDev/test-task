package ru.yandex.practicum.filmorate.storage.dal;

public interface ReviewRatingStorage {
    boolean addLikeDislike(long reviewId, long userId, boolean isUseful);
    boolean removeLikeDislike(long reviewId, long userId, boolean isUseful);
    long getReviewRating(long reviewId);
}
