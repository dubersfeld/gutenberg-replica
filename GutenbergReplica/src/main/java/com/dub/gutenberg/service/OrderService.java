package com.dub.gutenberg.service;

import java.util.Date;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.dub.gutenberg.domain.Address;
import com.dub.gutenberg.domain.Item;
import com.dub.gutenberg.domain.Order;
import com.dub.gutenberg.domain.PaymentMethod;

@PreAuthorize("hasAuthority('ROLE_USER')")
public interface OrderService {

	Order createOrder(Order order);
	Order getOrderById(String orderId);
	Order getActiveOrder(String userId);// Not in PRE_SHIPPING or SHIPPED state
	Order addBookToOrder(String orderId, String bookId);
	Order setState(String orderId, Order.State state);
	Order checkoutOrder(String orderId);
	Order recalculateTotal(String orderId, boolean payment);
	Order setPreShipping(String orderId);
	Order setCart(String orderId);
	Order editOrder(String orderId, List<Item> items);
	Order setShippingAddress(String orderId, Address address);
	Order setPaymentMethod(String orderId, PaymentMethod method);
		
	Order setDate(String orderId, Date date);
	
	void deleteAll();
	
}
