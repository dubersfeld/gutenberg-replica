package com.dub.gutenberg.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dub.gutenberg.domain.Review;

public interface ReviewRepository extends MongoRepository<Review, String> {

	List<Review> findByBookId(ObjectId bookId);
	
	List<Review> findByBookId(ObjectId bookId, Sort sort);
	
	List<Review> findByUserId(ObjectId userId);
}
