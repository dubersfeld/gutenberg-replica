package com.dub.spring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dub.spring.domain.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {

	Category findOneBySlug(String slug);
}
