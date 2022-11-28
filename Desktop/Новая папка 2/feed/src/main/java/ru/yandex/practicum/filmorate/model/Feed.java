package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Feed {
    private Long eventId;
    private Long createTime;
    private Integer userId;
    private String eventType;
    private String operation;
    private Long entityId;
}
