package com.dub.spring.controller.utils;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.dub.spring.domain.Address;
import com.dub.spring.domain.MyUser;
import com.dub.spring.domain.PaymentMethod;
import com.dub.spring.service.UserService;

public interface UserUtils {

	public MyUser getLoggedUser(HttpSession session);
	
	public void setLoggedUser(HttpSession session, MyUser user);
	
	public MyUser reload(HttpSession session);
	
	public MyUser createUser(String username, String password, 
			List<Address> addresses, List<PaymentMethod> paymentMethods,
			UserService userService);
}