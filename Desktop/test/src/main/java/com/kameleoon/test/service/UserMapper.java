package com.kameleoon.test.service;

import com.kameleoon.test.dto.UserDto;
import com.kameleoon.test.dto.UserDtoResponse;
import com.kameleoon.test.model.User;
import com.kameleoon.test.utilities.DateUtils;

public class UserMapper {

    public static UserDtoResponse toDtoResponse(UserDto user) {
        return UserDtoResponse.builder()
                .login(user.getLogin())
                .build();
    }

    public static User fromDto(UserDto user) {
        return User.builder()
                .login(user.getLogin())
                .email(user.getEmail())
                .password(user.getPassword())
                .created(DateUtils.now())
                .build();
    }
}
