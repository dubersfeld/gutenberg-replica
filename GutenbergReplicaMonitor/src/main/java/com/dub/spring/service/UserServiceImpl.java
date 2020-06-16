package com.dub.spring.service;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.dub.spring.domain.MyUser;
import com.dub.spring.domain.UserPrincipal;
import com.dub.spring.exceptions.DuplicateUserException;
import com.dub.spring.exceptions.UserNotFoundException;
import com.dub.spring.repository.UserRepository;


@Service
public class UserServiceImpl implements UserService {
	
	private static final Logger logger = 
			LoggerFactory.getLogger(UserServiceImpl.class);
	
	
	 private static final SecureRandom RANDOM;

	    static
	    {
	        try
	        {
	            RANDOM = SecureRandom.getInstanceStrong();
	        }
	        catch(NoSuchAlgorithmException e)
	        {
	            throw new IllegalStateException(e);
	        }
	    }

	    private static final int HASHING_ROUNDS = 10;

	@Autowired 
	private UserRepository userRepository;// which one?
	
	
	@Override
	public UserDetails loadUserByUsername(String username) {
		
		MyUser user = userRepository.getByUsername(username);

        UserPrincipal principal = new UserPrincipal(user);
    	
        return principal;   
    }

	 
	@Override   
	public MyUser saveUser(MyUser user, String newPassword) {
	    	 
		logger.info("Saving new user " + user.getUsername());
	        
		if(newPassword != null && newPassword.length() > 0) {   
			 String salt = BCrypt.gensalt(HASHING_ROUNDS, RANDOM);
	         user.setHashedPassword(
	                    BCrypt.hashpw(newPassword, salt).getBytes()
	         );   
		}
	        
		try {       	
			 return this.userRepository.save(user);
		} catch (Exception e) {	
			 String ex = ExceptionUtils.getRootCauseMessage(e);
			 logger.warn("Exception caught " + ex.toString());		 
			 throw new DuplicateUserException();
		 }
	}


	@Override
	public MyUser findByUsername(String username) {
		logger.warn("User reloaded from DB");
		return userRepository.getByUsername(username);
	}


	@Override
	public MyUser findById(String userId) {
		Optional<MyUser> user = userRepository.findById(userId);
		
		if (user.isEmpty()) {
			throw new UserNotFoundException();
		}
		return user.get();
	}


	@Override
	public void deleteAll() {
		userRepository.deleteAll();
	}


}
