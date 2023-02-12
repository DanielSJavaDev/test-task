package com.kameleoon.test.service;

import com.kameleoon.test.dto.UserDto;
import com.kameleoon.test.dto.UserDtoResponse;

public interface UserService {

    UserDtoResponse createUser(UserDto user);
}
