package com.dub.spring.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.dub.spring.domain.Address;
import com.dub.spring.domain.MyUser;
import com.dub.spring.domain.PaymentMethod;
import com.dub.spring.domain.UserPrincipal;
import com.dub.spring.exceptions.DuplicateUserException;
import com.dub.spring.repository.UserRepository;
import com.dub.spring.utils.MongoUtils;

@Service
public class UserServiceImpl implements UserService {
	
	private static final Logger logger = 
			LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private MongoUtils mongoUtils;
	
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
	private UserRepository userRepository;
	
	@Autowired 
	private MongoOperations mongoOperations;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) {
		
		MyUser user = userRepository.getByUsername(username);

        UserPrincipal principal = new UserPrincipal(user);
    	
        return principal;   
    }

	 
	@Override   
	public MyUser saveUser(MyUser user, String newPassword) {
	    	 
		logger.warn("Saving new user " + user.getUsername());
	        
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
	public MyUser setPrimaryPaymentMethod(String username, int index) {
		
		Query query = new Query();
		Update update = new Update();
		
		query.addCriteria(Criteria.where("username").is(username));
		update.set("mainPayMeth", index);
		
		MyUser user = mongoOperations.findAndModify(query, update, 
				new FindAndModifyOptions().returnNew(true), 
				MyUser.class);
		
		return user;
	}
	
	
	@Override
	public MyUser setPrimaryAddress(String username, int index) {

		Query query = new Query();
		Update update = new Update();
		
		query.addCriteria(Criteria.where("username").is(username));
		update.set("mainShippingAddress", index);
		
		MyUser user = mongoOperations.findAndModify(query, update, 
				new FindAndModifyOptions().returnNew(true), 
				MyUser.class);
			
		return user;
	}


	@Override
	public MyUser findById(String userId) {
		MyUser user = mongoUtils.getUser(userId);
		
		return user;
	}


	@Override
	public void deleteAll() {
		userRepository.deleteAll();
	}

	@Override
	public void addAddress(String username, Address newAddress) {
		Query query = new Query();
		Update update = new Update();
		query.addCriteria(Criteria.where("username").is(username));
		update.push("addresses", newAddress);
		mongoOperations.findAndModify(query, update, MyUser.class);
	}


	@Override
	public void deleteAddress(String username, Address address) {
		Query query = new Query();
		Update update = new Update();
		query.addCriteria(Criteria.where("username").is(username));
		update.pull("addresses", address);
		mongoOperations.findAndModify(query, update, MyUser.class);
	}


	@Override
	public void deletePaymentMethod(String username, PaymentMethod payMeth) {
		Query query = new Query();
		Update update = new Update();
		query.addCriteria(Criteria.where("username").is(username));
		update.pull("paymentMethods", payMeth);
		mongoOperations.findAndModify(query, update, MyUser.class);
		
	}


	@Override
	public void addPaymentMethod(String username, PaymentMethod newPayMeth) {
		Query query = new Query();
		Update update = new Update();
		query.addCriteria(Criteria.where("username").is(username));
		update.push("paymentMethods", newPayMeth);
		mongoOperations.findAndModify(query, update, MyUser.class);
	}

}
