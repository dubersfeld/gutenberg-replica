package com.dub.spring.controller.utils;

import javax.servlet.http.HttpSession;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dub.spring.domain.MyUser;
import com.dub.spring.domain.Order;
import com.dub.spring.service.OrderService;

@Component
public class OrderUtilsImpl implements OrderUtils {


	@Autowired
	private OrderService orderService;
	
	@Autowired
	private UserUtils userUtils;
	
	@Override
	public String getActiveOrderId(HttpSession session) {
				
		// first get logged user
		MyUser user = userUtils.getLoggedUser(session);
				
		if (session.getAttribute("activeOrderId") == null) {
			// first retrieve a persisted order from DB
			Order order = orderService.getActiveOrder(user.getId());
			
			if (order == null) {// no order found, create a new order
			
				order = new Order();
				order.setState(Order.State.CART);
				order.setUserId(new ObjectId(user.getId()));
				
				// initial creation
				order = orderService.createOrder(order);	
			}
			
			session.setAttribute("activeOrderId", order.getId());
		}
		
		return (String) session.getAttribute("activeOrderId");
	}

	@Override
	public void invalidActiveOrderId(HttpSession session) {
		if (session.getAttribute("activeOrderId") != null) {
			session.setAttribute("activeOrderId", null);
		}
	}
}