package com.portfolio.codereview.controller;

import com.portfolio.codereview.dto.CodeSubmissionRequest;
import com.portfolio.codereview.dto.ReviewResponseDto;
import com.portfolio.codereview.service.CodeReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final CodeReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseDto> analyzeCode(@Valid @RequestBody CodeSubmissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.performManualReview(request));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }
}
