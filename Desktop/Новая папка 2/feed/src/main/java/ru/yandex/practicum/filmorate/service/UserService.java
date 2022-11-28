package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.log.Logger;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.dal.FeedStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dal.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.dal.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;
    private final FilmStorage filmStorage;
    private final FeedStorage feedStorage;

    public Collection<User> getUsers() {
        Collection<User> usersInStorage = userStorage.getUsers();
        Logger.logSave(HttpMethod.GET, "/users", usersInStorage.toString());
        return usersInStorage;
    }

    public User addUser(User user) {
        User userInStorage = userStorage.addUser(checkValidation(user));
        Logger.logSave(HttpMethod.POST, "/users", userInStorage.toString());
        return userInStorage;
    }

    public User updateUser(User user) {
        User userInStorage = userStorage.updateUser(checkValidation(user));
        Logger.logSave(HttpMethod.PUT, "/users", userInStorage.toString());
        return userInStorage;
    }

    public User getUserById(long id) {
        User userInStorage = userStorage.getUserById(id);
        Logger.logSave(HttpMethod.GET, "/users/" + id, userInStorage.toString());
        return userInStorage;
    }

    public void removeUserById(long id) {
        if (userStorage.removeUserById(id)) {
            Logger.logSave(HttpMethod.DELETE,"/users/" + id, "User has deleted");
        } else {
            throw new ObjectNotFoundException(String.format("User with id %s not found", id));
        }
    }

    public void addAsFriend(long id, long friendId) {
        boolean addition;
        userStorage.getUserById(id);
        userStorage.getUserById(friendId);
        addition = friendsStorage.addAsFriend(id, friendId);
        if (addition) {
            feedStorage.addFeed(id, FeedTypes.FRIEND.toString(), FeedOperationTypes.ADD.toString(), friendId);
        }
        Logger.logSave(HttpMethod.PUT, "/users/" + id + "/friends/" + friendId, ((Boolean) addition).toString());
    }

    public void removeFromFriends(long id, long friendId) {
        boolean removal;
        userStorage.getUserById(id);
        userStorage.getUserById(friendId);
        removal = friendsStorage.removeFromFriends(id, friendId);
        if (!removal) {
            throw new ObjectNotFoundException(String.format("User with id %s is not friends with user with id %s",
                    id, friendId));
        }
        feedStorage.addFeed(id, FeedTypes.FRIEND.toString(), FeedOperationTypes.DELETE.toString(), friendId);
        Logger.logSave(HttpMethod.DELETE, "/users/" + id + "/friends/" + friendId, ((Boolean) removal).toString());
    }

    public List<User> getListOfFriends(long id) {
        userStorage.getUserById(id);
        List<User> friendList = friendsStorage.getListOfFriends(id).stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
        Logger.logSave(HttpMethod.GET, "/users/" + id + "/friends", friendList.toString());
        return friendList;
    }

    public List<User> getAListOfMutualFriends(long id, long otherId) {
        userStorage.getUserById(id);
        userStorage.getUserById(otherId);
        List<User> mutualFriends = friendsStorage.getAListOfMutualFriends(id, otherId).stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
        Logger.logSave(HttpMethod.GET, "/users/" + id + "/friends/common/" + otherId, mutualFriends.toString());
        return mutualFriends;
    }

    public List<Film> getRecommendations(long userId) {
        List<Film> recommendations = filmStorage.getRecommendations(userId);
        Logger.logSave(HttpMethod.GET, "/users/" + userId + "/recommendations", recommendations.toString());
        return recommendations;
    }

    private User checkValidation(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Login must not contain spaces");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

    public List<Feed> getUsersFeed(long userId) {
        List<Feed> feedInStorage = feedStorage.getFeedByUserId(userId);
        Logger.logSave(HttpMethod.GET, "/users/" + userId, feedInStorage.toString());
        return feedInStorage;
    }
}
