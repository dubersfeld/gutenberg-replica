package com.dub.spring.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.dub.spring.domain.Book;
import com.dub.spring.domain.BookRating;
import com.dub.spring.domain.Review;
import com.dub.spring.exceptions.NoReviewFoundException;
import com.dub.spring.repository.ReviewRepository;
import com.dub.spring.utils.MongoUtils;


@Service
public class ReviewServiceImpl implements ReviewService {
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private MongoOperations mongoOperations;
	
	@Autowired
	private MongoUtils mongoUtils;
	

	@Override
	public Review createReview(Review review) {
		
		// update Book
		Query query = new Query();
		Update update = new Update();
		
		query.addCriteria(Criteria.where("id").is(review.getBookId().toString()));
		update.inc("totalReviews", 1);
		update.push("ratings", review.getRating());
		
		mongoOperations.findAndModify(query, update, Book.class);
		
		Review newReview  = reviewRepository.save(review);
		
		return newReview;
	}


	@Override
	public void deleteAll() {
		reviewRepository.deleteAll();
	}


	@Override
	public List<Review> getReviewByBookId(String bookId) {
		List<Review> reviews = reviewRepository
							.findByBookId(new ObjectId(bookId));
		
		return reviews;
	}


	@Override
	public List<Review> getReviewByBookId(String bookId, String sortBy) {

		List<Review> reviews = reviewRepository.findByBookId(
														new ObjectId(bookId), Sort.by(Sort.Direction.DESC, sortBy));
		return reviews;
	}


	/** Aggregation demo */
	@Override
	public double getBookRating(String bookId) {
		
		GroupOperation group = group("bookId")
											.avg("rating").as("bookRating");
		 
		MatchOperation match = match(new Criteria("bookId").is(new ObjectId(bookId)));
		
		// static method, not constructor
		Aggregation aggregation = newAggregation(match, group);
		AggregationResults<BookRating> result = mongoOperations.aggregate(aggregation, "reviews", BookRating.class);
			
		if (result.getMappedResults().size() > 0) {
			return result.getMappedResults().get(0).getBookRating();
		} else {
			throw new NoReviewFoundException();
		}
	}
	

	@Override
	public boolean hasVoted(String reviewId, String userId) {
		Review review = mongoUtils.getReview(reviewId);	
		Set<ObjectId> voterIds = review.getVoterIds();
		return voterIds.contains(new ObjectId(userId));
	}


	@Override
	public void voteHelpful(String reviewId, String userId, boolean helpful) {
	
		Query query = new Query();
		Update update = new Update();
		
		query.addCriteria(Criteria.where("id").is(reviewId));
		update.inc("helpfulVotes", helpful ? 1 : 0);
		update.addToSet("voterIds", new ObjectId(userId));
			
		mongoOperations.findAndModify(query, update, 
						new FindAndModifyOptions().returnNew(true), 
						Review.class);
	}
}
