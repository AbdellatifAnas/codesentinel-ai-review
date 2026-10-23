package com.portfolio.codereview.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.codereview.dto.AiReviewResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiAiService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${app.gemini.api-key}")
    private String apiKey;

    @Value("${app.gemini.model}")
    private String model;

    public AiReviewResultDto analyzeCode(String language, String code) {
        if ("demo-mode".equals(apiKey) || apiKey.isBlank()) {
            log.info("Running in Demo Mode (Set GEMINI_API_KEY env var for live Gemini calls)");
            return new AiReviewResultDto(
                    72, 65,
                    "Demo Mode Analysis: Potential SQL injection and unclosed resource risks detected. Set GEMINI_API_KEY for live LLM analysis.",
                    "// Refactored secure version using PreparedStatement\nPreparedStatement stmt = connection.prepareStatement(\"SELECT * FROM users WHERE id = ?\");\nstmt.setLong(1, userId);",
                    List.of(
                            new AiReviewResultDto.IssueItem(1, "CRITICAL", "SECURITY", "Potential SQL Injection if raw string concatenation is used.", "Use parameterized queries or Spring Data JPA @Query bindings."),
                            new AiReviewResultDto.IssueItem(4, "MEDIUM", "CLEAN_CODE", "Missing null-safety validation on input parameters.", "Add Objects.requireNonNull() or Jakarta @NotNull validation.")
                    )
            );
        }

        String prompt = """
                You are a Principal Backend & Security Engineer conducting a strict code review.
                Analyze the following %s code/diff for bugs, security vulnerabilities (OWASP Top 10, SQLi, XSS, race conditions), performance bottlenecks (N+1 queries, memory leaks), and SOLID/Clean Code violations.

                Return strictly a valid JSON object matching this exact schema:
                {
                  "qualityScore": <integer 0-100>,
                  "securityScore": <integer 0-100>,
                  "summary": "<executive summary of code health>",
                  "refactoredSuggestion": "<improved version of the code>",
                  "issues": [
                    {
                      "lineNumber": <integer>,
                      "severity": "<CRITICAL|HIGH|MEDIUM|LOW>",
                      "category": "<SECURITY|PERFORMANCE|BUG|CLEAN_CODE>",
                      "description": "<clear explanation of the problem>",
                      "suggestedFix": "<concrete code fix>"
                    }
                  ]
                }

                Code to review:
                ```%s
                %s
                ```
                """.formatted(language, language, code);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of("responseMimeType", "application/json")
        );

        try {
            String rawResponse = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(rawResponse);
            String jsonText = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            return objectMapper.readValue(jsonText, AiReviewResultDto.class);
        } catch (Exception e) {
            log.error("Failed to call Gemini API", e);
            throw new RuntimeException("AI Code Analysis failed: " + e.getMessage(), e);
        }
    }
}
