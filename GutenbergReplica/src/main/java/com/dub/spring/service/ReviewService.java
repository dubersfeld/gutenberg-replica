package com.dub.spring.service;

import java.util.List;

import com.dub.spring.domain.Review;

public interface ReviewService {

	Review createReview(Review review);	
	List<Review> getReviewByBookId(String bookId);
	
	List<Review> getReviewByBookId(
								String bookId, 
								String sortBy);
	
	double getBookRating(String bookId);
	void voteHelpful(String reviewId, String userId, boolean helpful);
	boolean hasVoted(String reviewId, String userId);
	
	void deleteAll();
}
