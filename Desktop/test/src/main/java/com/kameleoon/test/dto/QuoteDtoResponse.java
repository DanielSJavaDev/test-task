package com.kameleoon.test.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Builder
@Data
@Jacksonized
public class QuoteDtoResponse {

    private String content;

    private String userLogin;
}
