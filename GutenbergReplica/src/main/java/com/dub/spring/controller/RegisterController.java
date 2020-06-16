package com.dub.spring.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dub.spring.controller.utils.UserUtils;
import com.dub.spring.domain.Address;
import com.dub.spring.domain.PaymentMethod;
import com.dub.spring.exceptions.DuplicateUserException;
import com.dub.spring.service.UserService;



@Controller
public class RegisterController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserUtils userUtils;
	
	  
	@GetMapping(value = "/register")    
	public String getRegisterForm(Model model) {
	    		
	   	model.addAttribute("profile", new RegisterForm());
	    	
	    return "register";   
	} 
	    
	    
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(@ModelAttribute("profile") RegisterForm form,
			Model model) {
	    			
	    List<Address> addresses = new ArrayList<>();
	    List<PaymentMethod> paymentMethods = new  ArrayList<>();
	    addresses.add(new Address(form.street, form.city, 
	    			form.zip, form.state, form.country));
	    paymentMethods.add(new PaymentMethod(form.getCardNumber(), 
	    			form.getNameOnCard()));
	    	
	    try {
	    	userUtils.createUser(form.getUsername(), form.getPassword(), 
			   					addresses, paymentMethods, userService);
	    	return "registerSuccess";
	    } catch (DuplicateUserException e) {
	    	model.addAttribute("error", "username already present");
	    	return "register";
	    }
	    
	} 
	    
	       
	public static class RegisterForm {
	    	
	    private String username;
	    private String password;
	    	
	    private String street = "";
	    private String city = "";
	    private String zip = "";
	    private String state = "";
	    private String country = "";
	    	
	    private String cardNumber;
	    private String nameOnCard;
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getStreet() {
			return street;
		}
		public void setStreet(String street) {
			this.street = street;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getZip() {
			return zip;
		}
		public void setZip(String zip) {
			this.zip = zip;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}
		public String getCardNumber() {
			return cardNumber;
		}
		public void setCardNumber(String cardNumber) {
			this.cardNumber = cardNumber;
		}
		public String getNameOnCard() {
			return nameOnCard;
		}
		public void setNameOnCard(String nameOnCard) {
			this.nameOnCard = nameOnCard;
		}   
	} 
}
