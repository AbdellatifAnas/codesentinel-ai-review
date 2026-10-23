import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CodeReview } from '../models/review.model';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/reviews';

  submitReview(payload: { title: string; language: string; code: string }): Observable<CodeReview> {
    return this.http.post<CodeReview>(this.apiUrl, payload);
  }

  getReviews(): Observable<CodeReview[]> {
    return this.http.get<CodeReview[]>(this.apiUrl);
  }
}
