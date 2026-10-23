package com.portfolio.codereview.dto;

import com.portfolio.codereview.entity.ReviewIssue;
import java.time.LocalDateTime;
import java.util.List;

public record ReviewResponseDto(
        Long id,
        String title,
        String language,
        String sourceType,
        String repositoryName,
        Integer pullRequestNumber,
        String submittedCode,
        Integer qualityScore,
        Integer securityScore,
        String summary,
        String refactoredSuggestion,
        List<ReviewIssue> issues,
        LocalDateTime createdAt
) {}
