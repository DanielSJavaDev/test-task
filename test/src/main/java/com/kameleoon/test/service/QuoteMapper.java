package com.kameleoon.test.service;

import com.kameleoon.test.dto.QuoteDto;
import com.kameleoon.test.dto.QuoteDtoResponse;
import com.kameleoon.test.model.Quote;
import com.kameleoon.test.model.User;
import com.kameleoon.test.utilities.DateUtils;

public class QuoteMapper {

    public static Quote create(String content, User user) {
        return Quote.builder()
                .content(content)
                .user(user)
                .lastUpdate(DateUtils.now())
                .build();
    }

    public static QuoteDto toDto(Quote quote) {
        return QuoteDto.builder()
                .content(quote.getContent())
                .userLogin(quote.getUser().getLogin())
                .votes(quote.getVotes())
                .build();
    }

    public static QuoteDtoResponse toDtoResponse(Quote quote, String login) {
        return QuoteDtoResponse.builder()
                .content(quote.getContent())
                .userLogin(login)
                .build();
    }
}
