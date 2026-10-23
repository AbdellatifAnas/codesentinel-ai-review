package com.portfolio.codereview.service;

import com.portfolio.codereview.dto.AiReviewResultDto;
import com.portfolio.codereview.dto.CodeSubmissionRequest;
import com.portfolio.codereview.dto.ReviewResponseDto;
import com.portfolio.codereview.entity.CodeReview;
import com.portfolio.codereview.repository.CodeReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodeReviewServiceTest {

    @Mock
    private CodeReviewRepository repository;

    @Mock
    private GeminiAiService geminiAiService;

    @InjectMocks
    private CodeReviewService codeReviewService;

    @Test
    void performManualReview_ShouldAnalyzeAndPersistReview() {
        CodeSubmissionRequest request = new CodeSubmissionRequest(
                "UserAuthService.java", "Java", "String sql = \"SELECT * FROM users WHERE id = \" + id;"
        );
        AiReviewResultDto mockAiResponse = new AiReviewResultDto(
                45, 20, "SQL Injection detected", "Use PreparedStatement",
                List.of(new AiReviewResultDto.IssueItem(1, "CRITICAL", "SECURITY", "SQLi risk", "Use parameterized query"))
        );

        when(geminiAiService.analyzeCode("Java", request.code())).thenReturn(mockAiResponse);
        when(repository.save(any(CodeReview.class))).thenAnswer(invocation -> {
            CodeReview saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        ReviewResponseDto response = codeReviewService.performManualReview(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.securityScore()).isEqualTo(20);
        assertThat(response.issues()).hasSize(1);
        verify(repository, times(1)).save(any(CodeReview.class));
    }
}
