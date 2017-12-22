package com.dub.gutenberg.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dub.gutenberg.domain.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {

	Category findOneBySlug(String slug);
}
