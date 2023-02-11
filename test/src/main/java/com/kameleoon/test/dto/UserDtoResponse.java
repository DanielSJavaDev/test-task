package com.kameleoon.test.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDtoResponse {

    private String login;
}
