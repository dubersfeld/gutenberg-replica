package com.dub.gutenberg.controller.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dub.gutenberg.controller.books.DisplayOthers;
import com.dub.gutenberg.controller.display.DisplayItem;
import com.dub.gutenberg.controller.display.DisplayItemPrice;
import com.dub.gutenberg.controller.reviews.ReviewWithAuthor;
import com.dub.gutenberg.domain.Book;
import com.dub.gutenberg.domain.Item;
import com.dub.gutenberg.domain.Order;
import com.dub.gutenberg.domain.Review;
import com.dub.gutenberg.service.BookService;
import com.dub.gutenberg.service.OrderService;
import com.dub.gutenberg.service.ReviewService;
import com.dub.gutenberg.service.UserService;

@Component
public class DisplayUtilsImpl implements DisplayUtils {

	@Autowired 
	private UserService userService;
	
	@Autowired 
	private OrderService orderService;
	
	@Autowired 
	private BookService bookService;
	
	@Autowired 
	private ReviewService reviewService;
	

	public List<ReviewWithAuthor> getReviewWithAuthors(List<Review> reviews, String userId)  {
		List<ReviewWithAuthor> reviewWithAuthors = new ArrayList<>();
		
		for (Review review : reviews) {
			String authorId = review.getUserId().toString();
			String authorname = userService.findById(authorId).getUsername();
			ReviewWithAuthor rwa = new ReviewWithAuthor(review, authorname);
			reviewWithAuthors.add(rwa);
			rwa.setVotedByUser(reviewService.hasVoted(review.getId(), userId));
		}
		
		return reviewWithAuthors;
		
	}


	@Override
	public List<DisplayOthers> getOtherBooksBoughtWith(Order order) {
		// find other books frequently bought with books in order
		List<DisplayOthers> others = new ArrayList<>();
				
		for (Item item : order.getLineItems()) {
			String bookId = item.getBookId();
			List<Book> otherBooks = bookService.getBooksBoughtWith(bookId, 3);
			if (!otherBooks.isEmpty()) {
				others.add(new DisplayOthers(
						bookService.getBookById(bookId), otherBooks));
			}
		}
							
		return others;
	}
	
	@Override
	public List<DisplayItem> getDisplayItems(String orderId) {
		
		Order order = orderService.getOrderById(orderId);
		List<Item> items = order.getLineItems();
		List<DisplayItem> displayItems = new ArrayList<>();
							
		for (Item item : items) {
			Book dBook = bookService.getBookById(item.getBookId());
		
			DisplayItem dItem = new DisplayItem(item);
			
			dItem.setTitle(dBook.getTitle());
			displayItems.add(dItem);
		}
		
		return displayItems;
	}

	@Override
	public List<DisplayItemPrice> getDisplayItemPrices(String orderId) {
		Order order = orderService.getOrderById(orderId);
		List<Item> items = order.getLineItems();
		List<DisplayItemPrice> itemsWithPrices = new ArrayList<>();
							
		for (Item item : items) {
			Book dBook = bookService.getBookById(item.getBookId());
		
			double price = item.getQuantity() * dBook.getPrice()/100.0;
			DisplayItemPrice dip = new DisplayItemPrice(item);
			
			dip.setTitle(dBook.getTitle());
			dip.setPrice(price);
			itemsWithPrices.add(dip);
		}
		
		return itemsWithPrices;
	}
}