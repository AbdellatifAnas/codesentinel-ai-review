import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReviewService } from './services/review.service';
import { CodeReview } from './models/review.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  private reviewService = inject(ReviewService);

  reviews: CodeReview[] = [];
  selectedReview: CodeReview | null = null;
  isLoading = false;

  form = {
    title: 'OrderPaymentService.java',
    language: 'Java',
    code: 'public User findUser(String userId) {\n    String sql = "SELECT * FROM users WHERE id = " + userId;\n    return jdbcTemplate.queryForObject(sql, User.class);\n}'
  };

  ngOnInit(): void {
    this.loadReviews();
  }

  loadReviews(): void {
    this.reviewService.getReviews().subscribe(data => {
      this.reviews = data;
      if (data.length > 0 && !this.selectedReview) {
        this.selectedReview = data[0];
      }
    });
  }

  analyzeCode(): void {
    if (!this.form.code.trim()) return;
    this.isLoading = true;
    this.reviewService.submitReview(this.form).subscribe({
      next: (result) => {
        this.reviews.unshift(result);
        this.selectedReview = result;
        this.isLoading = false;
      },
      error: () => (this.isLoading = false)
    });
  }
}
