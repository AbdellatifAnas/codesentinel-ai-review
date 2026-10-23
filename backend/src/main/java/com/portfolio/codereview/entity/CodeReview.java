package com.portfolio.codereview.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "code_reviews")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CodeReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String sourceType;

    private String repositoryName;
    private Integer pullRequestNumber;

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String submittedCode;

    private Integer qualityScore;
    private Integer securityScore;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String refactoredSuggestion;

    @OneToMany(mappedBy = "codeReview", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<ReviewIssue> issues = new ArrayList<>();

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void addIssue(ReviewIssue issue) {
        issues.add(issue);
        issue.setCodeReview(this);
    }
}
