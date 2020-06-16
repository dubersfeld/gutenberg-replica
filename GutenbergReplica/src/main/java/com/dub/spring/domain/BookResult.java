package com.dub.spring.domain;

import org.bson.types.ObjectId;

public class BookResult {
	
	ObjectId bookId;

	public ObjectId getBookId() {
		return bookId;
	}

	public void setBookId(ObjectId bookId) {
		this.bookId = bookId;
	}


	
}
