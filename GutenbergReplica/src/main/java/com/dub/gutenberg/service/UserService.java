package com.dub.gutenberg.service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.dub.gutenberg.domain.Address;
import com.dub.gutenberg.domain.MyUser;
import com.dub.gutenberg.domain.PaymentMethod;

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
	 
	 //MyUser updateAdress(String userId, Address newAddress, int index);
	 MyUser setPrimaryPaymentMethod(String username, int index);
	 MyUser setPrimaryAddress(String username, int index);
	 
	 void deleteAll();
	 
	 // more advanced methods
	 void deleteAddress(String username, Address address);
	 void addAddress(String username, Address newAddress);
	 
	 void deletePaymentMethod(String username, PaymentMethod payMeth);
	 void addPaymentMethod(String username, PaymentMethod payMeth);
	 
}
