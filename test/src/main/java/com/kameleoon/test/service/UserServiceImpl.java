package com.kameleoon.test.service;

import com.kameleoon.test.dto.UserDto;
import com.kameleoon.test.dto.UserDtoResponse;
import com.kameleoon.test.exception.DuplicateException;
import com.kameleoon.test.model.User;
import com.kameleoon.test.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDtoResponse createUser(UserDto user) {
        User existedUser = userRepository.findUser(user.getEmail());
        if (existedUser != null) {
            log.warn("existed email " + user.getEmail());
            throw new DuplicateException("Email " + user.getEmail() + " already exist");
        }
        existedUser = UserMapper.fromDto(user);
        try {
            userRepository.save(existedUser);
            log.info("user with login " + user.getLogin() + " created");
            return UserMapper.toDtoResponse(user);
        } catch (DataIntegrityViolationException e) {
            log.warn("User " + user.getLogin() + " already exist");
            throw new DuplicateException("User " + user.getLogin() + " already exist");
        }
    }
}
