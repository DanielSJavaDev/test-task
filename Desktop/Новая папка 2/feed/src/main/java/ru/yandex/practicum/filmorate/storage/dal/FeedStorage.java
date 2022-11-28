package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {
    List<Feed> addFeed(Long userId, String event, String operation, Long entityId);
    List<Feed> getFeedByUserId(long userId);
}
