package com.dub.gutenberg.controller.utils;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.dub.gutenberg.domain.Address;
import com.dub.gutenberg.domain.MyUser;
import com.dub.gutenberg.domain.PaymentMethod;
import com.dub.gutenberg.service.UserService;

public interface UserUtils {

	public MyUser getLoggedUser(HttpSession session);
	
	public void setLoggedUser(HttpSession session, MyUser user);
	
	public MyUser reload(HttpSession session);
	
	public MyUser createUser(String username, String password, 
			List<Address> addresses, List<PaymentMethod> paymentMethods,
			UserService userService);
}