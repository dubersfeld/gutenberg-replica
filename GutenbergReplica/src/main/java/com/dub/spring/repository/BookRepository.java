package com.dub.spring.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dub.spring.domain.Book;

public interface BookRepository extends MongoRepository<Book, String> {

	List<Book> findByCategoryId(ObjectId categoryId, Sort sort);
	
	Book findOneBySlug(String bookSlug);	
}
