package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.log.Logger;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dal.MpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public Collection<Mpa> getMpa() {
        Collection<Mpa> mpaInStorage = mpaStorage.getMpa();
        Logger.logSave(HttpMethod.GET, "/mpa", mpaInStorage.toString());
        return mpaInStorage;
    }

    public Mpa getMpaById(int id) {
        Mpa mpaInStorage = mpaStorage.getMpaById(id);
        Logger.logSave(HttpMethod.GET, "/mpa/" + id, mpaInStorage.toString());
        return mpaInStorage;
    }
}
