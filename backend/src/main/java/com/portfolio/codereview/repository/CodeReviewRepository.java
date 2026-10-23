package com.portfolio.codereview.repository;

import com.portfolio.codereview.entity.CodeReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CodeReviewRepository extends JpaRepository<CodeReview, Long> {
    List<CodeReview> findAllByOrderByCreatedAtDesc();
}
