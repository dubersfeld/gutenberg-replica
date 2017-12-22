package com.dub.gutenberg.service;

import java.util.List;

import com.dub.gutenberg.domain.Category;

public interface CategoryService {

	public List<Category> getAllCategories();
	
	public List<Category> getLeaveCategories();
	
	public Category getCategory(String categorySlug);
}
