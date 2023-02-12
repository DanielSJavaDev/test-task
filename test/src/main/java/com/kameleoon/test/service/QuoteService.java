package com.kameleoon.test.service;

import com.kameleoon.test.dto.QuoteDto;
import com.kameleoon.test.dto.QuoteDtoResponse;

import java.util.List;

public interface QuoteService {

    List<QuoteDto> getQuotes(boolean isRandom, boolean isAsc);

    QuoteDtoResponse createQuote(Long userId, String content);

    QuoteDto updateQuote(Long userId, Long quoteId, String content);

    boolean deleteQuote(Long userId, Long quoteId);

    boolean addVote(Long userId, boolean isPositive, Long quoteId);
}
