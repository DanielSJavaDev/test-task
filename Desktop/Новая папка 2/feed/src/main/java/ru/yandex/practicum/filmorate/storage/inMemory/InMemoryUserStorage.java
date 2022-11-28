package ru.yandex.practicum.filmorate.storage.inMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.RedoCreationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private long id = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        if (users.containsKey(user.getId())) {
            throw new RedoCreationException("User already exists");
        }
        if (user.getId() == 0) {
            generateId();
            user.setId(id);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        userExistenceCheck(user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(long id) {
        userExistenceCheck(id);
        return users.get(id);
    }

    @Override
    public boolean removeUserById(long id) {
        return false;
    }

    private void generateId() {
        id++;
    }

    private void userExistenceCheck(long id) {
        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException(String.format("User with id %s not found", id));
        }
    }
}
