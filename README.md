# 🛡️ CodeSentinel AI — Automated Code Review & PR Security Engine



An enterprise-grade AI code auditing platform built with **Java 17**, **Spring Boot 3**, **Angular**, and **Google Gemini API**. CodeSentinel AI analyzes source code and GitHub Pull Request diffs in real time to detect OWASP security vulnerabilities, N+1 query bottlenecks, concurrency bugs, and Clean Code violations.

## 🚀 Key Backend Engineering Highlights
- **Asynchronous GitHub Webhook Engine:** Processes `pull_request` events asynchronously (`@Async`) with **HMAC-SHA256 cryptographic signature validation** (`X-Hub-Signature-256`) and posts line-by-line review comments directly on GitHub PRs.
- **Structured LLM Orchestration:** Uses Spring 6's modern `RestClient` and JSON schema enforcement with **Gemini 2.5 Flash** to deterministically parse AI outputs into strongly typed Java 17 `record` DTOs.
- **RFC 7807 Error Handling:** Centralized `@RestControllerAdvice` returning standardized `ProblemDetail` payloads.
- **OpenAPI 3 / Swagger UI:** Auto-generated interactive REST documentation at `http://localhost:8080/swagger-ui.html`.

## ⚡ Quick Start in IntelliJ IDEA
1. Open the root folder in IntelliJ IDEA and load `backend/pom.xml` as a Maven project.
2. Set environment variable `GEMINI_API_KEY=your_key` in your Run Configuration (or test immediately with the built-in offline demo fallback).
3. Run `AiCodeReviewApplication` — starts out-of-the-box on port `8080` using H2 in-memory DB (or switch to `prod` profile for MySQL).
