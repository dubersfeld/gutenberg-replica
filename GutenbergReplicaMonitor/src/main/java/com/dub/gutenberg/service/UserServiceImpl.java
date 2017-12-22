package com.dub.gutenberg.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.dub.gutenberg.admin.repository.UserRepository;
import com.dub.gutenberg.domain.MyUser;
import com.dub.gutenberg.domain.UserPrincipal;
import com.dub.gutenberg.exceptions.DuplicateUserException;


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
		MyUser user = userRepository.findOne(userId);
		return user;
	}


	@Override
	public void deleteAll() {
		userRepository.deleteAll();
	}


}

