package com.dub.gutenberg.domain;

import org.bson.types.ObjectId;

/**
 * clean this stuff
 * */
public class UserResult {
	
	ObjectId userId;

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

}
