package com.portfolio.codereview.service;

import com.portfolio.codereview.dto.AiReviewResultDto;
import com.portfolio.codereview.dto.CodeSubmissionRequest;
import com.portfolio.codereview.dto.ReviewResponseDto;
import com.portfolio.codereview.entity.CodeReview;
import com.portfolio.codereview.entity.ReviewIssue;
import com.portfolio.codereview.repository.CodeReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeReviewService {

    private final CodeReviewRepository repository;
    private final GeminiAiService geminiAiService;
    private final GitHubService gitHubService;

    @Transactional
    public ReviewResponseDto performManualReview(CodeSubmissionRequest request) {
        AiReviewResultDto aiResult = geminiAiService.analyzeCode(request.language(), request.code());
        CodeReview saved = saveReviewEntity(
                request.title(), request.language(), "MANUAL", null, null, request.code(), aiResult
        );
        return mapToDto(saved);
    }

    @Async
    @Transactional
    public void processGitHubPullRequest(String repoFullName, int prNumber, String prTitle) {
        log.info("Processing GitHub PR #{} for repo {}", prNumber, repoFullName);
        String diff = gitHubService.fetchPullRequestDiff(repoFullName, prNumber);
        AiReviewResultDto aiResult = geminiAiService.analyzeCode("Git Diff", diff);

        saveReviewEntity(prTitle, "Git Diff", "GITHUB_PR", repoFullName, prNumber, diff, aiResult);

        StringBuilder markdown = new StringBuilder();
        markdown.append("## 🤖 CodeSentinel AI Review\n\n");
        markdown.append("**Quality Score:** `").append(aiResult.qualityScore()).append("/100` | ");
        markdown.append("**Security Score:** `").append(aiResult.securityScore()).append("/100`\n\n");
        markdown.append("### Summary\n").append(aiResult.summary()).append("\n\n");
        markdown.append("### Identified Issues\n");
        for (AiReviewResultDto.IssueItem issue : aiResult.issues()) {
            markdown.append("- **[").append(issue.severity()).append(" - ").append(issue.category())
                    .append("]** (Line ").append(issue.lineNumber()).append("): ")
                    .append(issue.description()).append("\n  - *Suggested Fix:* `").append(issue.suggestedFix()).append("`\n");
        }

        gitHubService.postPullRequestComment(repoFullName, prNumber, markdown.toString());
    }

    public List<ReviewResponseDto> getAllReviews() {
        return repository.findAllByOrderByCreatedAtDesc().stream().map(this::mapToDto).toList();
    }

    public ReviewResponseDto getReviewById(Long id) {
        return repository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + id));
    }

    private CodeReview saveReviewEntity(String title, String language, String sourceType,
                                        String repoName, Integer prNumber, String code,
                                        AiReviewResultDto aiResult) {
        CodeReview review = CodeReview.builder()
                .title(title)
                .language(language)
                .sourceType(sourceType)
                .repositoryName(repoName)
                .pullRequestNumber(prNumber)
                .submittedCode(code)
                .qualityScore(aiResult.qualityScore())
                .securityScore(aiResult.securityScore())
                .summary(aiResult.summary())
                .refactoredSuggestion(aiResult.refactoredSuggestion())
                .build();

        if (aiResult.issues() != null) {
            for (AiReviewResultDto.IssueItem item : aiResult.issues()) {
                review.addIssue(ReviewIssue.builder()
                        .lineNumber(item.lineNumber())
                        .severity(item.severity())
                        .category(item.category())
                        .description(item.description())
                        .suggestedFix(item.suggestedFix())
                        .build());
            }
        }
        return repository.save(review);
    }

    private ReviewResponseDto mapToDto(CodeReview entity) {
        return new ReviewResponseDto(
                entity.getId(), entity.getTitle(), entity.getLanguage(), entity.getSourceType(),
                entity.getRepositoryName(), entity.getPullRequestNumber(), entity.getSubmittedCode(),
                entity.getQualityScore(), entity.getSecurityScore(), entity.getSummary(),
                entity.getRefactoredSuggestion(), entity.getIssues(), entity.getCreatedAt()
        );
    }
}
