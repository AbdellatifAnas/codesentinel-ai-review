package com.portfolio.codereview.dto;

import java.util.List;

public record AiReviewResultDto(
        int qualityScore,
        int securityScore,
        String summary,
        String refactoredSuggestion,
        List<IssueItem> issues
) {
    public record IssueItem(
            int lineNumber,
            String severity,
            String category,
            String description,
            String suggestedFix
    ) {}
}
