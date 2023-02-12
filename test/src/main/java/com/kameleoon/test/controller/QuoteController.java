package com.kameleoon.test.controller;

import com.kameleoon.test.dto.QuoteDto;
import com.kameleoon.test.dto.QuoteDtoResponse;
import com.kameleoon.test.service.QuoteService;
import com.kameleoon.test.utilities.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/quotes")
@RequiredArgsConstructor
public class QuoteController {
    private final QuoteService service;

    @GetMapping
    public List<QuoteDto> getQuotes(@RequestParam(required = false, defaultValue = "false") boolean isRandom,
                                    @RequestParam(required = false, defaultValue = "true") boolean isAsc) {
        return service.getQuotes(isRandom, isAsc);
    }

    @PostMapping
    public QuoteDtoResponse createQuote(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                        @RequestBody String content) {
        return service.createQuote(userId, content);
    }

    @PatchMapping("/{quoteId}")
    public QuoteDto updateQuote(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                @PathVariable("quoteId") Long quoteId,
                                @RequestBody String content) {
        return service.updateQuote(userId, quoteId, content);
    }

    @DeleteMapping("/{quoteId}")
    public boolean deleteQuote(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                               @PathVariable("quoteId") Long quoteId) {
        return service.deleteQuote(userId, quoteId);
    }

    @PostMapping("/vote/{quoteId}")
    public boolean addVote(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                           @RequestParam(required = false, defaultValue = "true") boolean isPositive,
                           @PathVariable("quoteId") Long quoteId) {
        return service.addVote(userId, isPositive, quoteId);
    }
}