package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.log.Logger;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping//получить полный список пользователей
    public Collection<User> getUsers() {
        Logger.logRequest(HttpMethod.GET, "/users", "no body");
        return userService.getUsers();
    }

    @PostMapping//добавить пользователя
    public User addUser(@Valid @RequestBody User user) {
        Logger.logRequest(HttpMethod.POST, "/users", user.toString());
        return userService.addUser(user);
    }

    @PutMapping//обновить пользователя или добавить пользователя (если он не был добавлен ранее)
    public User updateUser(@Valid @RequestBody User user) {
        Logger.logRequest(HttpMethod.PUT, "/users", user.toString());
        return userService.updateUser(user);
    }

    @GetMapping("/{id}") //получить пользователя по id
    public User getUserById(@PathVariable long id) {
        Logger.logRequest(HttpMethod.GET, "/users/" + id, "no body");
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void removeUserById(@PathVariable long id) {
        Logger.logRequest(HttpMethod.DELETE, "/users/" + id, "no body");
        userService.removeUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")//добавить в друзья
    public void addAsFriend(@PathVariable long id,
                               @PathVariable long friendId) {
        Logger.logRequest(HttpMethod.PUT, "/users/" + id + "/friends/" + friendId, "no body");
        userService.addAsFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")//удалить из друзей
    public void removeFromFriends(@PathVariable long id,
                                     @PathVariable long friendId) {
        Logger.logRequest(HttpMethod.DELETE, "/users/" + id + "/friends/" + friendId, "no body");
        userService.removeFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")//получить список друзей
    public List<User> getListOfFriends(@PathVariable long id) {
        Logger.logRequest(HttpMethod.GET, "/users/" + id + "/friends", "no body");
        return userService.getListOfFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")//получить список общих друзей
    public List<User> getAListOfMutualFriends(@PathVariable long id,
                                              @PathVariable long otherId) {
        Logger.logRequest(HttpMethod.GET, "/users/" + id + "/friends/common/" + otherId, "no body");
        return userService.getAListOfMutualFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable long id) {
        Logger.logRequest(HttpMethod.GET, "/users/" + id + "/recommendations", "no body");
        return userService.getRecommendations(id);
    }

    @GetMapping("/{id}/feed")
    public List<Feed> getUsersFeed(@PathVariable long id) { //получить ленту событий
        Logger.logRequest(HttpMethod.GET, "/users/" + id + "/feed", "no body");
        return userService.getUsersFeed(id);
    }
}
