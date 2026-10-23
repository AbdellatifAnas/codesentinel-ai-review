export interface ReviewIssue {
  id?: number;
  lineNumber: number;
  severity: 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW';
  category: 'SECURITY' | 'PERFORMANCE' | 'BUG' | 'CLEAN_CODE';
  description: string;
  suggestedFix: string;
}

export interface CodeReview {
  id: number;
  title: string;
  language: string;
  sourceType: string;
  qualityScore: number;
  securityScore: number;
  summary: string;
  submittedCode: string;
  refactoredSuggestion: string;
  issues: ReviewIssue[];
  createdAt: string;
}
