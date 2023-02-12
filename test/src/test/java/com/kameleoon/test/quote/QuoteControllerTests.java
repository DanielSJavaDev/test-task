package com.kameleoon.test.quote;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kameleoon.test.config.QuoteControllerTestConfig;
import com.kameleoon.test.config.WebConfig;
import com.kameleoon.test.controller.QuoteController;
import com.kameleoon.test.dto.QuoteDto;
import com.kameleoon.test.dto.QuoteDtoResponse;
import com.kameleoon.test.model.Quote;
import com.kameleoon.test.model.User;
import com.kameleoon.test.repositories.QuoteRepository;
import com.kameleoon.test.repositories.UserRepository;
import com.kameleoon.test.service.QuoteService;
import com.kameleoon.test.utilities.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig({QuoteController.class, QuoteControllerTestConfig.class, WebConfig.class})
public class QuoteControllerTests {

    @Mock
    private QuoteService quoteService;

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuoteController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private Quote quote;
    private QuoteDtoResponse quoteDtoResponse;
    private QuoteDto quoteDto;
    private User user;
    private String content = "content";

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        user = User.builder()
                .id(1L)
                .login("login")
                .email("e@mail.com")
                .build();
        quote = Quote.builder()
                .id(1L)
                .user(user)
                .content("content")
                .votes(1L)
                .build();
        quoteDto = QuoteDto.builder()
                .content(quote.getContent())
                .userLogin(quote.getUser().getLogin())
                .build();
        quoteDtoResponse = QuoteDtoResponse.builder()
                .content(quote.getContent())
                .userLogin(quote.getUser().getLogin())
                .build();
    }

    @Test
    void createQuoteTest() throws Exception {
        when(quoteService.createQuote(any(), any()))
                .thenReturn(quoteDtoResponse);

        mvc.perform(post("/quotes").header(Constants.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(content))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userLogin", is(quoteDtoResponse.getUserLogin())))
                .andExpect(jsonPath("$.content", is(quoteDtoResponse.getContent())));
    }

    @Test void updateQuoteTest() throws Exception {
        QuoteDto quoteAfterUpdate = quoteDto;
        quoteAfterUpdate.setContent("after update");
        when(quoteService.updateQuote(any(), any(), any()))
                .thenReturn(quoteAfterUpdate);

        mvc.perform(patch("/quotes/1")
                        .content(mapper.writeValueAsString(content))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        when(quoteRepository.findQuoteById(any()))
                .thenReturn(quote);

        when(userRepository.findUserById(any()))
                .thenReturn(user);

        mvc.perform(patch("/quotes/1").header(Constants.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(content))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is(quoteAfterUpdate.getContent())));
    }

    @Test void deleteUserTest() throws Exception {
        when(quoteService.deleteQuote(any(), any()))
                .thenReturn(true);
        when(quoteRepository.findQuoteById(any()))
                .thenReturn(quote);

        when(userRepository.findUserById(any()))
                .thenReturn(user);

        mvc.perform(delete("/quotes/1").header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }
}