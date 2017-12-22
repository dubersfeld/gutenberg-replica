package com.dub.gutenberg.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dub.gutenberg.domain.Category;
import com.dub.gutenberg.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Override
	public List<Category> getAllCategories() {
		
		return categoryRepository.findAll();
	}

	@Override
	public List<Category> getLeaveCategories() {
		List<Category> cats = getAllCategories();
		List<Category> leaves = new ArrayList<>();
		
		for (Category cat : cats) {
			if (cat.getChildren().isEmpty()) {
				leaves.add(cat);
			}
		}
		return leaves;
	}

	@Override
	public Category getCategory(String categorySlug) {
		return categoryRepository.findOneBySlug(categorySlug);
	}

}
