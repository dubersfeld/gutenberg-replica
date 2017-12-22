package com.dub.gutenberg.controller.payments;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dub.gutenberg.controller.utils.OrderUtils;
import com.dub.gutenberg.domain.Book;
import com.dub.gutenberg.domain.Order;
import com.dub.gutenberg.domain.Payment;
import com.dub.gutenberg.domain.PaymentMethod;
import com.dub.gutenberg.exceptions.TotalChangedException;
import com.dub.gutenberg.service.BookService;
import com.dub.gutenberg.service.OrderService;
import com.dub.gutenberg.service.PaymentService;



@Controller
public class PaymentController {
	
	private static final Logger logger = 
			LoggerFactory.getLogger(PaymentController.class);
		
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private OrderUtils orderUtils;
	
	@Autowired 
	private PaymentService paymentService;
	
	@RequestMapping(value = "/authorizePayment", method = RequestMethod.POST)
	public String authorizePayment(
			@Valid @ModelAttribute("paymentForm") PaymentMethod form,
			BindingResult result, ModelMap model,
			HttpServletRequest request) throws InterruptedException {
		
		if (result.hasErrors()) {
			return "payments/authorize";
		} else {
			HttpSession session = request.getSession();
			
			String orderId = orderUtils.getActiveOrderId(session);
			
			Order order = orderService.getOrderById(orderId);
			
			// change state to PRE_AUTHORIZE
			if (order.getState() == Order.State.CART) {
				order = orderService.checkoutOrder(order.getId());
			}
					
			Book book = bookService.getBookBySlug("apes-wrath-4153");
			bookService.setPrice(book.getId(), 10000);
			
			Thread.sleep(5000);
			
			// recalculate totals
			try {
				order = orderService
							.recalculateTotal(order.getId(), true);
				Payment payment = new Payment();
				payment.setAmount(order.getSubtotal()/100.0);
				payment.setCardNumber(form.getCardNumber());
				payment.setCardName(form.getName());
				
				boolean paymentSuccess = paymentService.authorizePayment(payment);
				
				// transition order state to CART in case of payment failure
				if (!paymentSuccess) {
					order = orderService
							.setCart(order.getId());
				}
				
				session.setAttribute("paymentSuccess", paymentSuccess);
				// redirect to OrderController 
				return "redirect:/payment";
			} catch (TotalChangedException e) {	
				logger.warn("Exception caught " + e);
				// set order state back to CART
				orderService.setCart(orderId);
				return "redirect:/getCart";
			}
		}
	}
}

