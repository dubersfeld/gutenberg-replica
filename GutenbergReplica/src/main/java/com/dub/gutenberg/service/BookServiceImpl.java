package com.dub.gutenberg.service;

import static com.dub.gutenberg.controller.DateCorrect.correctDate;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.dub.gutenberg.domain.Book;
import com.dub.gutenberg.domain.BookCount;
import com.dub.gutenberg.domain.BookUser;
import com.dub.gutenberg.domain.Category;
import com.dub.gutenberg.domain.Review;
import com.dub.gutenberg.domain.UserResult;
import com.dub.gutenberg.repository.BookRepository;
import com.dub.gutenberg.repository.CategoryRepository;
import com.dub.gutenberg.repository.ReviewRepository;

@Service
public class BookServiceImpl implements BookService {

	@Autowired
	private BookRepository bookRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private MongoOperations mongoOperations;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	@Override
	public Book getBookBySlug(String slug) {
		return bookRepository.findOneBySlug(slug);
	}


	@Override
	public Book getBookById(String bookId) {
		return bookRepository.findOne(bookId);
	}


	@Override
	public Book setPrice(String bookId, int price) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(bookId));
		Update update = new Update();
		update.set("price", price);
		
		Book book = mongoOperations.findAndModify(
										query, 
										update, 
										Book.class);
		// return old document
		return book;
	}


	@Override
	public List<Book> allBooksByCategory(String categorySlug, String sortBy) {
			
		// find category id
		Category cat = categoryRepository.findOneBySlug(categorySlug);
			
		List<Book> books = bookRepository.findByCategoryId(
							new ObjectId(cat.getId()), new Sort(Sort.Direction.ASC, sortBy));
		
		return books;
	}


	@Override
	public void deleteAll() {
		bookRepository.deleteAll();
	}


	@Override
	public Book createBook(Book book) {
		return bookRepository.save(book);
	}


	@Override
	public List<Book> getBooksBoughtWith(String bookId, int outLimit) {
		
		// first aggregation: find all users who bought the book referenced by bookId
		
		MatchOperation match1 = match(Criteria.where("state").is("SHIPPED"));		
		ProjectionOperation proj1 = project("lineItems", "userId");
		UnwindOperation unwind = unwind("lineItems");
		MatchOperation match2 = match(Criteria.where("lineItems.bookId").is(bookId));	
		ProjectionOperation proj2 = project("userId");
				
		Aggregation aggregation = Aggregation.newAggregation(match1, proj1, unwind, match2, proj2);
		AggregationResults<UserResult> result = mongoOperations.aggregate(aggregation, "orders", UserResult.class);
		
		// result contains all users who bought the book referenced by bookId
		
		List<ObjectId> userIds = new ArrayList<>();
			
		for (UserResult bu : result.getMappedResults()) {
			userIds.add(bu.getUserId());
		}
			
		// second aggregation: find all books bought by the users returned by the first aggregation
		
		match1 = match(Criteria.where("state").is("SHIPPED")
												.and("userId").in(userIds));
	
		GroupOperation group = group("bookId").count().as("count");
		
		proj2 = project("count").and("bookId").previousOperation();
		
		ProjectionOperation projAlias = project("userId")							
									.and("lineItems.bookId").as("bookId");
		
		match2 = match(Criteria.where("bookId").ne(bookId));
		
		SortOperation sort = sort(Sort.Direction.DESC, "count");
		LimitOperation limitOp = limit(outLimit);
		
		aggregation = newAggregation(match1, unwind, projAlias, match2, group, proj2, sort, limitOp);
		
		AggregationResults<BookCount> bookCounts = mongoOperations.aggregate(
											aggregation, "orders", BookCount.class);
		
		
		// building the actual output List<Book>
		List<BookCount> bookCountList = bookCounts.getMappedResults();
		
		List<Book> outBooks = new ArrayList<>();
		
		for (BookCount bookCount : bookCountList) { 
			outBooks.add(bookRepository.findOne(bookCount.getBookId()));
		}
	
		return outBooks;
	}


	@Override
	public List<Book> getBooksNotReviewed(String userId, int outLimit) throws ParseException {
	
		/** 
		 * first step: find all books 
		 * already reviewed by user referenced by userId 
		 * */
		List<Review> reviews = reviewRepository.findByUserId(new ObjectId(userId));
		
		List<String> reviewedBookIds = new ArrayList<>();
		
		for (Review review : reviews) {
			reviewedBookIds.add(review.getBookId().toString());
		}
		
		/** second step: find all books 
		 * recently bought by user referenced by userId 
		 * that were not reviewed by user yet
		 * */
		UnwindOperation unwind = unwind("lineItems");
		
		// set up limit date in application.properties or better add an admin page
		Date limitDate = correctDate(sdf.parse("2017-04-24"));

		MatchOperation match1 = match(Criteria.where("state").is("SHIPPED")
											.and("date").gte(limitDate)
											.and("userId").is(new ObjectId(userId)));
										
		MatchOperation match2 = match(Criteria.where("bookId").nin(reviewedBookIds));
		GroupOperation group = group("bookId").last("userId").as("userId");			
					
		ProjectionOperation proj1 = project("lineItems", "userId");
		ProjectionOperation proj2 = project("userId").and("bookId").previousOperation();
		ProjectionOperation projAlias = project("userId")							
					.and("lineItems.bookId").as("bookId");
		
		LimitOperation limitOp = limit(outLimit);
		
		
		Aggregation aggregation = newAggregation(match1, proj1, unwind, projAlias, group, proj2, match2, limitOp);

		AggregationResults<BookUser> bookResults = mongoOperations.aggregate(
									aggregation, "orders", BookUser.class);
			
		List<Book> booksToReview = new ArrayList<>();
		for (BookUser bu : bookResults) {	
			booksToReview.add(bookRepository.findOne(bu.getBookId()));
		}

		return booksToReview;
	}
}
