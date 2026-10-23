package com.portfolio.codereview.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.codereview.service.CodeReviewService;
import com.portfolio.codereview.service.GitHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks/github")
@RequiredArgsConstructor
public class GitHubWebhookController {

    private final GitHubService gitHubService;
    private final CodeReviewService reviewService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<String> handlePullRequestWebhook(
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestHeader(value = "X-GitHub-Event") String event,
            @RequestBody String payload) throws Exception {

        /*if (!gitHubService.verifySignature(payload, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }*/

        if ("pull_request".equals(event)) {
            JsonNode root = objectMapper.readTree(payload);
            String action = root.path("action").asText();
            if ("opened".equals(action) || "synchronize".equals(action)) {
                int prNumber = root.path("number").asInt();
                String prTitle = root.path("pull_request").path("title").asText();
                String repoFullName = root.path("repository").path("full_name").asText();

                reviewService.processGitHubPullRequest(repoFullName, prNumber, prTitle);
                return ResponseEntity.accepted().body("PR review queued");
            }
        }
        return ResponseEntity.ok("Event ignored");
    }
}
