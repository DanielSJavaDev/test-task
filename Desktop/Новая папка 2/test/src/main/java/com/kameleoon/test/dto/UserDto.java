package com.kameleoon.test.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Builder
@Data
@Jacksonized
public class UserDto {

    private String login;

    private char[] password;

    private String email;
}
