package com.dub.gutenberg.service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.dub.gutenberg.domain.MyUser;



public interface UserService extends UserDetailsService {

	 @Override   
	 UserDetails loadUserByUsername(String username);// custom implementation

	 MyUser findById(String userId);// not override	
	 MyUser findByUsername(String username);// not override
	 
	 MyUser saveUser(
	            @NotNull(message = "{validate.authenticate.saveUser}") @Valid
	                MyUser user,
	            String newPassword
	    );
	 
	 
	 void deleteAll();
	
}
