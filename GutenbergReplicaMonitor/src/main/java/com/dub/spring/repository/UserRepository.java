package com.dub.spring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dub.spring.domain.MyUser;


public interface UserRepository extends MongoRepository<MyUser, String> {

	MyUser getByUsername(String username);
	
}