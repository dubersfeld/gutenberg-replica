package com.dub.spring.controller.orders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dub.spring.controller.books.DisplayOthers;
import com.dub.spring.controller.display.DisplayItem;
import com.dub.spring.controller.display.DisplayItemPrice;
import com.dub.spring.controller.payments.PaymentController;
import com.dub.spring.controller.utils.DisplayUtils;
import com.dub.spring.controller.utils.OrderUtils;
import com.dub.spring.controller.utils.UserUtils;
import com.dub.spring.domain.Address;
import com.dub.spring.domain.Book;
import com.dub.spring.domain.Item;
import com.dub.spring.domain.MyUser;
import com.dub.spring.domain.Order;
import com.dub.spring.domain.PaymentMethod;
import com.dub.spring.service.BookService;
import com.dub.spring.service.OrderService;


@Controller
public class OrderController {
	
	private static final Logger logger = 
			LoggerFactory.getLogger(PaymentController.class);
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private UserUtils userUtils;
	
	@Autowired
	private OrderUtils orderUtils;
	

	@Autowired
	private DisplayUtils displayUtils;
		
		
	@RequestMapping(value ="/getCart", method= RequestMethod.GET)
	public String getCart(Model model, HttpServletRequest request) {
				
		try {
			HttpSession session = request.getSession();
			String orderId = orderUtils.getActiveOrderId(session);
				
			Order order = orderService.getOrderById(orderId);
		
			// Here order cannot be null
			
			if (order.getState() == Order.State.PRE_AUTHORIZE) {
				return "redirect:/payment";
			}
				
			List<DisplayItemPrice> items = displayUtils.getDisplayItemPrices(orderId);
			
			model.addAttribute("items", items);
			model.addAttribute("subtotal", order.getSubtotal()/100.0);
		
			return "orders/cart";
		} catch (AccessDeniedException e) {
			logger.warn("Exception caught " + e);
			return "accessDenied";
		}
	}
	
	
	@RequestMapping(value = "/addToCart", method = RequestMethod.POST)
	public String addToCart(@Valid @ModelAttribute("book") Book book,
			BindingResult result, Model model,
			HttpServletRequest request) {
		
		try {
			HttpSession session = request.getSession();
		
			if (session.getAttribute("placed") != null) {
				session.setAttribute("placed", null);
			}
			
			// retrieve book
			book = bookService.getBookBySlug(book.getSlug());
		
			String orderId = orderUtils.getActiveOrderId(session);
					
			Order order = orderService.getOrderById(orderId);
		
			// here order cannot be null						
			order = orderService.addBookToOrder(orderId, book.getId());
					
			// preparing order display
			List<DisplayItemPrice> items = displayUtils.getDisplayItemPrices(orderId);
			
			model.addAttribute("items", items);
			model.addAttribute("subtotal", order.getSubtotal()/100.0);
		
			return "orders/cart";
		} catch (AccessDeniedException e) {
			logger.warn("Exception caught " + e);
			return "accessDenied";
		}
	}
		
	
	@RequestMapping(value = "/payment", method = RequestMethod.GET)
	public String payment(Model model, HttpServletRequest request) {
	
		try {
			HttpSession session = request.getSession();
			Boolean placed = false;
			Order order = null;
		
			/** 
			 * Initially placed is null 
			 * placed not null only after redirection from authorize
			 * */
			if (session.getAttribute("placed") != null) {
				placed = (Boolean)session.getAttribute("placed"); 
			}
		
			MyUser user = userUtils.getLoggedUser(session);
		
			String orderId = orderUtils.getActiveOrderId(session);
			
			order = orderService.getOrderById(orderId);
		
			// preparing order display
		
			List<DisplayItemPrice> items = displayUtils.getDisplayItemPrices(orderId);
		
			double total = order.getSubtotal()/100.0;
						
			model.addAttribute("items", items);
		
			Address shipAdd;
		
			if (session.getAttribute("shipAdd") != null) {// from redirection
				shipAdd = (Address)session.getAttribute("shipAdd");	
			} else if (user.getAddresses() == null) {
				model.addAttribute("error", "You don't have any address"); 
				return "payments/checkoutFailure";
			} else {
				shipAdd = user.getAddresses()
								.get(user.getMainShippingAddress());
			}
		
			model.addAttribute("address", shipAdd);
		
			PaymentMethod payMeth;
		
			if (session.getAttribute("payMeth") != null) {// from redirection
				payMeth = (PaymentMethod)session.getAttribute("payMeth");
			} else if (user.getPaymentMethods() == null) {
				model.addAttribute("error", "You don't have any payment method"); 
				return "payments/checkoutFailure";
			} else {
				payMeth = user.getPaymentMethods()
								.get(user.getMainPayMeth());
			}
		
			model.addAttribute("payMeth", payMeth);			
			model.addAttribute("total", total);
		
			if (session.getAttribute("paymentSuccess") != null) {// from redirection
				placed = (Boolean)session.getAttribute("paymentSuccess");
				model.addAttribute("placed", placed);
				model.addAttribute("denied", !placed);
				if (placed) {// success
					// add actual shipping address to order before persisting
					orderService.setShippingAddress(orderId, shipAdd);
					// actual payment method
					orderService.setPaymentMethod(orderId, payMeth);
					orderService.setPreShipping(orderId);
					orderService.setDate(orderId, new Date());
					//session.setAttribute("activeOrderId", null);
					orderUtils.invalidActiveOrderId(session);
					session.setAttribute("paymentSuccess", null);
				} else {// failure, state transition to CART 
					orderService.setCart(orderId);
				}
			}
		
			// find other books frequently bought with these books
		
			List<DisplayOthers> others = displayUtils
								.getOtherBooksBoughtWith(order);
		
			model.addAttribute("others", others);
		
			return "orders/payment";
		} catch (AccessDeniedException e) {
			logger.warn("Exception caught " + e);
			return "accessDenied";
		}
	}
	
	
	@RequestMapping(value ="/editCart", method= RequestMethod.GET)		
	public String getEditCart(@RequestParam("redirectUrl") String redirect,
			Model model, HttpServletRequest request) {
		
		try {
			HttpSession session = request.getSession();
			String orderId = orderUtils.getActiveOrderId(session);
			
			Order order = orderService.getOrderById(orderId);
		
			// For display only
			// Here both subclasses are required
			List<DisplayItem> editItems = displayUtils.getDisplayItems(orderId);
			List<DisplayItemPrice> items = displayUtils.getDisplayItemPrices(orderId);
			
			OrderEditForm form = new OrderEditForm();
			
			form.setItems(editItems);
				
			// saving redirection string for the next step
			session.setAttribute("redirect", redirect);
		
			model.addAttribute("orderEditForm", form);// already populated form
			model.addAttribute("items", items);
			model.addAttribute("subtotal", order.getSubtotal()/100.0);
		
			return "orders/editCart";
		} catch (AccessDeniedException e) {
			logger.warn("Exception caught " + e);
			return "accessDenied";
		}
	}

	@RequestMapping(value ="/editCart", method= RequestMethod.POST)
	public String editCart(
			@Valid @ModelAttribute("orderEditForm") OrderEditForm orderEditForm, 
			BindingResult result, Model model, HttpServletRequest request) {
		if (result.hasErrors()) {
			return "orders/editCart";
		} else {
			try {
				HttpSession session = request.getSession();
				List<? extends Item> editItems = orderEditForm.getItems();
		
				List<Item> items = new ArrayList<>();
					
				for (Item item : editItems) {
					if (item.getQuantity() > 0) {
						items.add(item);
					}
				}
			
				String orderId = orderUtils.getActiveOrderId(session);
					
				// now update order
				orderService.editOrder(orderId, items);
				
				String redirect = (String)session.getAttribute("redirect");
		
				return "redirect:" + redirect;
			} catch (AccessDeniedException e) {
				logger.warn("Exception caught " + e);
				return "accessDenied";
			}
		}
	}
	
	
	public static class OrderEditForm {
		
		private List<? extends Item> items;
		
		public OrderEditForm() {
			items = new ArrayList<>();
		}

		public List<? extends Item> getItems() {
			return items;
		}

		public void setItems(List<? extends Item> items) {
			this.items = items;
		}
	
	}
	
}
