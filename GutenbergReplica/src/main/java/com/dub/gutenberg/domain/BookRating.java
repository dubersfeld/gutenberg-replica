package com.dub.gutenberg.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class BookRating {
	
	@Id
	private ObjectId bookId;
	private Double bookRating;
	

	public Double getBookRating() {
		return bookRating;
	}
	public void setBookRating(Double bookRating) {
		this.bookRating = bookRating;
	}
	public ObjectId getBookId() {
		return bookId;
	}
	public void setBookId(ObjectId bookId) {
		this.bookId = bookId;
	}

}
