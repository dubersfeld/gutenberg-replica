package com.dub.spring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dub.spring.domain.Order;


public interface OrderRepository extends MongoRepository<Order, String> {

	
}
