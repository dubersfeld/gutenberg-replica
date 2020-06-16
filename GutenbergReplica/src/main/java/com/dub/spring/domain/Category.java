package com.dub.spring.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="categories")
public class Category {
	
	@Id
	private String id;
	
	private String slug;
	
	private String name;
	
	private String description;
	
	private ObjectId parentId;
	
	private List<ObjectId> children;
	
	private List<Map<String, Object>> ancestors;
	
	public Category() {
		this.ancestors = new ArrayList<>();
		this.children = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Map<String, Object>> getAncestors() {
		return ancestors;
	}

	public void setAncestors(List<Map<String, Object>> ancestors) {
		this.ancestors = ancestors;
	}

	public ObjectId getParentId() {
		return parentId;
	}

	public void setParentId(ObjectId parentId) {
		this.parentId = parentId;
	}

	public List<ObjectId> getChildren() {
		return children;
	}

	public void setChildren(List<ObjectId> children) {
		this.children = children;
	}
	
	
	
	
	
	

}
