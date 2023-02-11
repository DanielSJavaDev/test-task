package com.kameleoon.test.service;

import com.kameleoon.test.dto.QuoteDto;
import com.kameleoon.test.dto.QuoteDtoResponse;
import com.kameleoon.test.dto.UserDto;
import com.kameleoon.test.dto.UserDtoResponse;
import com.kameleoon.test.exception.DuplicateException;
import com.kameleoon.test.exception.QuoteNotFoundException;
import com.kameleoon.test.exception.UserNotFoundException;
import com.kameleoon.test.model.Quote;
import com.kameleoon.test.model.User;
import com.kameleoon.test.repositories.QuoteRepository;
import com.kameleoon.test.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final QuoteRepository quoteRepository;
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

    @Override
    public LinkedList<QuoteDto> getQuotes(boolean isRandom, boolean isAsc) {
        LinkedList<QuoteDto> response = new LinkedList<>();
        if (isRandom) {
            Quote randomQuote = quoteRepository.findRandom();
            response.addFirst(QuoteMapper.toDto(randomQuote));
        }
        List<QuoteDto> quoteDtos;
        if (isAsc) {
            quoteDtos = quoteRepository.findTop10asc(PageRequest.of(0,10)).stream()
                    .map(QuoteMapper::toDto).collect(Collectors.toList());
        } else {
            quoteDtos = quoteRepository.findTop10desc(PageRequest.of(0,10)).stream()
                    .map(QuoteMapper::toDto).collect(Collectors.toList());
        }
        response.addAll(quoteDtos);
        log.info("all quotes returned");
        return response;
    }

    @Override
    @Transactional
    public QuoteDtoResponse createQuote(Long userId, String content) {
        User user = userValidation(userId);
        Quote quote = QuoteMapper.create(content, user);
        quote.setVotes(0L);
        quoteRepository.save(quote);
        log.info("quote was crated by user " + userId);
        return QuoteMapper.toDtoResponse(quote, user.getLogin());
    }

    @Override
    @Transactional
    public QuoteDto updateQuote(Long userId, Long quoteId, String content) {
        userValidation(userId);
        Quote quote = quoteValidation(quoteId);
        quote.setContent(content);
        return QuoteMapper.toDto(quoteRepository.save(quote));
    }

    @Override
    @Transactional
    public boolean deleteQuote(Long userId, Long quoteId) {
        userValidation(userId);
        Quote quote = quoteValidation(quoteId);
        quoteRepository.delete(quote);
        log.info("quote with id" + quoteId + "was deleted");
        return true;
    }

    @Override
    @Transactional
    public boolean addVote(Long userId, boolean isPositive, Long quoteId) {
        userValidation(userId);
        Quote quote = quoteValidation(quoteId);
        if (isPositive) {
            quote.setVotes(quote.getVotes() + 1);
        } else {
            quote.setVotes(quote.getVotes() - 1);
        }
        quoteRepository.save(quote);
        log.info("vote added to quote " + quoteId);
        return true;
    }

    private User userValidation(Long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            log.warn("unknown user " + userId);
            throw new UserNotFoundException("can't find user with id " + userId);
        }
        return user;
    }

    private Quote quoteValidation(Long quoteId) {
        Quote quote = quoteRepository.findQuoteById(quoteId);
        if (quote == null) {
            log.warn("unknown quote " + quoteId);
            throw new QuoteNotFoundException("can't find quote with id " + quoteId);
        }
        return quote;
    }
}
