package ru.yandex.practicum.filmorate.storage.dal;

import java.util.List;

public interface FriendsStorage {
    boolean addAsFriend(long userId, long friendId);
    boolean removeFromFriends(long userId, long friendId);
    List<Long> getListOfFriends(long userId);
    List<Long> getAListOfMutualFriends(long userId, long otherId);

}
