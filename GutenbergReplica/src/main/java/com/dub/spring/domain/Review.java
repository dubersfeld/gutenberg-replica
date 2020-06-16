package com.dub.spring.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/** 
 * A given user can write only one review for the same book
 * */
@Document(collection="reviews")

@CompoundIndexes({
    @CompoundIndex(name = "product_user", 
    					def = "{'bookId' : 1, 'userId': 1}", 
    					unique = true)
})
public class Review {
	
	@Id
	private String id;
	
	private ObjectId bookId;
	private Date date;
	private String title;
	private String text;
	private int rating;
	private ObjectId userId;
	private String username;
	private int helpfulVotes;
	private Set<ObjectId> voterIds;
	
	public Review() {
		this.voterIds = new HashSet<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	public ObjectId getBookId() {
		return bookId;
	}

	public void setBookId(ObjectId bookId) {
		this.bookId = bookId;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public int getHelpfulVotes() {
		return helpfulVotes;
	}

	public void setHelpfulVotes(int helpfulVotes) {
		this.helpfulVotes = helpfulVotes;
	}

	public Set<ObjectId> getVoterIds() {
		return voterIds;
	}

	public void setVoterIds(Set<ObjectId> voterIds) {
		this.voterIds = voterIds;
	}
	
}
