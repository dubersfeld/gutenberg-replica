package com.dub.spring.controller;

public class ExceptionParseResult {

	private String address;
	private String type;
	private String state;
	
	public ExceptionParseResult(String address, String type, String state) {
		this.address = address;
		this.type = type;
		this.state = state;
	}
	
	public ExceptionParseResult() {
		
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	
	
}
