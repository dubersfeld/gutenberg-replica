package com.dub.gutenberg.controller.display;

import com.dub.gutenberg.domain.Item;


public class DisplayItemPrice extends DisplayItem {

	protected double price;
	
	public DisplayItemPrice(Item source) {
		super(source);
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}	
}
