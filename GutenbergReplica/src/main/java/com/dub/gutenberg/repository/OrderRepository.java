package com.dub.gutenberg.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dub.gutenberg.domain.Order;


public interface OrderRepository extends MongoRepository<Order, String> {

	
}
