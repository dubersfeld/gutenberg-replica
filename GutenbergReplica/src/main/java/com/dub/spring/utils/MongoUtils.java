package com.dub.spring.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dub.spring.domain.Book;
import com.dub.spring.domain.MyUser;
import com.dub.spring.domain.Order;
import com.dub.spring.domain.Review;
import com.dub.spring.exceptions.BookNotFoundException;
import com.dub.spring.exceptions.OrderNotFoundException;
import com.dub.spring.exceptions.ReviewNotFoundException;
import com.dub.spring.exceptions.UserNotFoundException;
import com.dub.spring.repository.BookRepository;
import com.dub.spring.repository.OrderRepository;
import com.dub.spring.repository.ReviewRepository;
import com.dub.spring.repository.UserRepository;

// helper class 
@Service
public class MongoUtils {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private BookRepository bookRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;

	public Order getOrder(String orderId) {	
		if (!orderRepository.existsById(orderId)) {
			throw new OrderNotFoundException();
		}
			
		return orderRepository.findById(orderId).get();
	}
	
	public Book getBook(String bookId) {	
		if (!bookRepository.existsById(bookId)) {
			throw new BookNotFoundException();
		}
				
		return bookRepository.findById(bookId).get();
	}
	
	public MyUser getUser(String userId) {	
		if (!userRepository.existsById(userId)) {
				throw new UserNotFoundException();
		}
					
		return userRepository.findById(userId).get();
	}
	
	public Review getReview(String reviewId) {	
		if (!reviewRepository.existsById(reviewId)) {
				throw new ReviewNotFoundException();
		}
					
		return reviewRepository.findById(reviewId).get();
	}
	
}
