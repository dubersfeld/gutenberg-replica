package com.dub.gutenberg.controller.display;

import com.dub.gutenberg.domain.Item;


public class DisplayItem extends Item {

	protected String title;
	
	public DisplayItem(Item source)  {
		super(source);
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
