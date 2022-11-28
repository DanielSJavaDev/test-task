package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.log.Logger;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dal.DirectorStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Collection<Director> getDirectors() {
        Collection<Director> directorsInStorage = directorStorage.getDirectors();
        Logger.logSave(HttpMethod.GET, "/films/directors", directorsInStorage.toString());
        return directorsInStorage;
    }

    public Director addDirector(Director director) {
        Director directorInStorage = directorStorage.addDirector(director);
        Logger.logSave(HttpMethod.POST, "/films/directors", directorInStorage.toString());
        return directorInStorage;
    }

    public Director updateDirector(Director director) {
        Director directorInStorage = directorStorage.updateDirector(director);
        Logger.logSave(HttpMethod.PUT, "/films/directors", directorInStorage.toString());
        return directorInStorage;
    }

    public Director getDirectorById(long id) {
        Director directorInStorage = directorStorage.getDirectorById(id);
        Logger.logSave(HttpMethod.GET, "/films/directors/" + id, directorInStorage.toString());
        return directorInStorage;
    }

    public void removeDirectorById(long id) {
        if ( directorStorage.removeDirectorById(id)) {
            Logger.logSave(HttpMethod.DELETE,"/films/directors/" + id, "User has deleted");
        } else {
            throw new ObjectNotFoundException(String.format("Director with id %s not found", id));
        }
    }
}
