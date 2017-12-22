package com.dub.gutenberg.controller.utils;

import java.util.List;

import com.dub.gutenberg.controller.books.DisplayOthers;
import com.dub.gutenberg.controller.display.DisplayItem;
import com.dub.gutenberg.controller.display.DisplayItemPrice;
import com.dub.gutenberg.controller.reviews.ReviewWithAuthor;
import com.dub.gutenberg.domain.Order;
import com.dub.gutenberg.domain.Review;

public interface DisplayUtils {

	public List<ReviewWithAuthor> getReviewWithAuthors(List<Review> reviews, String userId);
	public List<DisplayOthers> getOtherBooksBoughtWith(Order order);

	List<DisplayItem> getDisplayItems(String orderId);
	List<DisplayItemPrice> getDisplayItemPrices(String orderId);
}
