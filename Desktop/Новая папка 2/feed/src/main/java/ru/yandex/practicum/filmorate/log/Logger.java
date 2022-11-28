package ru.yandex.practicum.filmorate.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

@Slf4j
public class Logger {

    public static void logRequest(HttpMethod method, String uri, String body) {
        log.info("Endpoint request received: '{} {}'. Request body: '{}'", method, uri, body);
    }

    public static void logSave(HttpMethod method, String uri, String storage) {
        log.info("Endpoint request result: '{} {}'. In storage: '{}'", method, uri, storage);
    }
}
