package com.portfolio.codereview.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review_issues")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer lineNumber;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false)
    private String category;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String suggestedFix;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_review_id")
    @JsonIgnore
    private CodeReview codeReview;
}
