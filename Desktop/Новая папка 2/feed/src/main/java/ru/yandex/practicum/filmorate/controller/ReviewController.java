package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.log.Logger;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping//добавить отзыв
    public Review addReview(@Valid @RequestBody Review review) {
        Logger.logRequest(HttpMethod.POST, "/reviews", review.toString());
        return reviewService.addReview(review);
    }

    @PutMapping//обновить отзыв
    public Review updateReview(@Valid @RequestBody Review review) {
        Logger.logRequest(HttpMethod.PUT, "/reviews", review.toString());
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}") //удалить отзыв по id
    public void removeReview(@PathVariable long id) {
        Logger.logRequest(HttpMethod.DELETE, "/reviews/" + id, "no body");
        reviewService.removeReview(id);
    }

    @GetMapping("/{id}") //получить отзыв по id
    public Review getReviewById(@PathVariable long id) {
        Logger.logRequest(HttpMethod.GET, "/reviews/" + id, "no body");
        return reviewService.getReviewById(id);
    }

    //получить все отзы по id фильма
    //если фильм не указан то все отзывы
    //если кол-во не указано то 10
    @GetMapping
    public List<Review> getReviews(@RequestParam(defaultValue = "0") long filmId,
                                   @RequestParam(defaultValue = "10") @Positive int count) {
        Logger.logRequest(HttpMethod.GET, "/reviews?filmId=" + filmId + "&count=" + count, "no body");
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")//поставить лайк отзыву
    public void likeReview(@PathVariable long id,
                           @PathVariable long userId) {
        Logger.logRequest(HttpMethod.PUT, "/reviews/" + id + "/like/" + userId, "no body");
        reviewService.likeReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")//поставить дизлайк отзыву
    public void dislikeReview(@PathVariable long id,
                           @PathVariable long userId) {
        Logger.logRequest(HttpMethod.PUT, "/reviews/" + id + "/dislike/" + userId, "no body");
        reviewService.dislikeReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}") //удалить лайк отзыва
    public void removeLikeReview(@PathVariable long id,
                                 @PathVariable long userId) {
        Logger.logRequest(HttpMethod.DELETE, "/reviews/" + id + "/like/" + userId, "no body");
        reviewService.removeLikeReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}") //удалить дизлайк отзыва
    public void removeDislikeReview(@PathVariable long id,
                                 @PathVariable long userId) {
        Logger.logRequest(HttpMethod.DELETE, "/reviews/" + id + "/dislike/" + userId, "no body");
        reviewService.removeDislikeReview(id, userId);
    }
}
