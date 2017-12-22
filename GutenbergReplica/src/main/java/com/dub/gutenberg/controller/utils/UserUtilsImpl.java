package com.dub.gutenberg.controller.utils;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.dub.gutenberg.domain.Address;
import com.dub.gutenberg.domain.MyUser;
import com.dub.gutenberg.domain.PaymentMethod;
import com.dub.gutenberg.service.UserService;

@Component
public class UserUtilsImpl implements UserUtils {
	
	private static final Logger logger = 
			LoggerFactory.getLogger(UserUtils.class);
		

	@Autowired
	private UserService userService;
	
	@Override
	public MyUser getLoggedUser(HttpSession session) {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		if (session.getAttribute("loggedUser") != null) {
			return (MyUser)session.getAttribute("loggedUser");
		} else {
			MyUser user = userService.findByUsername(auth.getName());
			session.setAttribute("loggedUser", user);
			return user;
		}
	}
	
	@Override
	public MyUser reload(HttpSession session) {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		logger.warn("reload user " + auth.getName());
		MyUser user = userService.findByUsername(auth.getName());
		session.setAttribute("loggedUser", user);
		return user;
		
	}
	
	@Override
	public MyUser createUser(String username, String password, 
			List<Address> addresses, List<PaymentMethod> paymentMethods,
			UserService userService) {
		
		MyUser user = new MyUser();
		user.setUsername(username);
		user.setEnabled(true);
		user.setAccountNonLocked(true);
		user.setCredentialsNonExpired(true);
		user.setAccountNonExpired(true);
		
		user.setAddresses(addresses);

		user.setPaymentMethods(paymentMethods);
		
		return userService.saveUser(user, password);
	}

	@Override
	public void setLoggedUser(HttpSession session, MyUser user) {
		session.setAttribute("loggedUser", user);
		
	}
}