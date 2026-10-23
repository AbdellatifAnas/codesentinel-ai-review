package com.portfolio.codereview.dto;

import jakarta.validation.constraints.NotBlank;

public record CodeSubmissionRequest(
        @NotBlank(message = "Title is required") String title,
        @NotBlank(message = "Language is required") String language,
        @NotBlank(message = "Code snippet cannot be empty") String code
) {}
