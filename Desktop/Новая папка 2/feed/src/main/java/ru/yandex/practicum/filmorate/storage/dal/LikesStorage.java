package ru.yandex.practicum.filmorate.storage.dal;

import java.util.List;

public interface LikesStorage {
    boolean addLike(long filmId, long userId);
    boolean unlike(long filmId, long userId);
    List<Long> getListOfLikes(long filmId);
    List<Long> getTheBestFilms(int count);

}
