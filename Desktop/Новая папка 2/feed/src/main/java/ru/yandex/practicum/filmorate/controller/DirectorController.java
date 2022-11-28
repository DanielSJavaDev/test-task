package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.log.Logger;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping//получить полный список режиссеров
    public Collection<Director> getDirectors() {
        Logger.logRequest(HttpMethod.GET, "/directors/", "no body");
        return directorService.getDirectors();
    }

    @PostMapping//добавить режиссера
    public Director addDirector(@Valid @RequestBody Director director) {
        Logger.logRequest(HttpMethod.POST, "/directors/", director.toString());
        return directorService.addDirector(director);
    }

    @PutMapping//обновить режиссера
    public Director updateDirector(@Valid @RequestBody Director director) {
        Logger.logRequest(HttpMethod.PUT, "/directors/", director.toString());
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")//удалить режиссера
    public void deleteDirector(@PathVariable long id) {
        Logger.logRequest(HttpMethod.DELETE, "/directors/" + id, "no body");
        directorService.removeDirectorById(id);
    }

    @GetMapping("/{id}") //получить режиссера по id
    public Director getDirectorById(@PathVariable long id) {
        Logger.logRequest(HttpMethod.GET, "/directors/" + id, "no body");
        return directorService.getDirectorById(id);
    }
}
