package com.portfolio.codereview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubService {

    private final RestClient restClient;

    @Value("${app.github.token}")
    private String githubToken;

    @Value("${app.github.webhook-secret}")
    private String webhookSecret;

    public boolean verifySignature(String payload, String signatureHeader) {
        if (signatureHeader == null || !signatureHeader.startsWith("sha256=")) {
            return false;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = "sha256=" + HexFormat.of().formatHex(hash);
            return MessageDigest.isEqual(
                    expectedSignature.getBytes(StandardCharsets.UTF_8),
                    signatureHeader.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            log.error("Webhook signature verification error", e);
            return false;
        }
    }

    public String fetchPullRequestDiff(String repoFullName, int prNumber) {
        return restClient.get()
                .uri("https://api.github.com/repos/" + repoFullName + "/pulls/" + prNumber)
                .header("Authorization", "Bearer " + githubToken)
                .header("Accept", "application/vnd.github.v3.diff")
                .retrieve()
                .body(String.class);
    }

    public void postPullRequestComment(String repoFullName, int prNumber, String markdownComment) {
        restClient.post()
                .uri("https://api.github.com/repos/" + repoFullName + "/issues/" + prNumber + "/comments")
                .header("Authorization", "Bearer " + githubToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("body", markdownComment))
                .retrieve()
                .toBodilessEntity();
    }
}
