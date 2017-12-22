package com.dub.gutenberg.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dub.gutenberg.domain.MyUser;


public interface UserRepository extends MongoRepository<MyUser, String> {

	MyUser getByUsername(String username);
	

}
