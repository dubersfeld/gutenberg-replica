package com.dub.spring.service;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.dub.spring.domain.Address;
import com.dub.spring.domain.Book;
import com.dub.spring.domain.Item;
import com.dub.spring.domain.Order;
import com.dub.spring.domain.PaymentMethod;
import com.dub.spring.exceptions.TotalChangedException;
import com.dub.spring.repository.OrderRepository;
import com.dub.spring.utils.MongoUtils;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private MongoOperations mongoOperation;
	
	@Autowired
	private MongoUtils mongoUtils;
	
	
	@Override
	public Order createOrder(Order order) {
		Order newOrder = orderRepository.save(order);
		return newOrder;
	}

	

	@Override
	public Order addBookToOrder(String orderId, String bookId) {

		//Order order = getOrder(orderId);
		Order order = mongoUtils.getOrder(orderId);
		
		// first add new item if not already present
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(orderId)
				.and("state").is(Order.State.CART)
				.and("lineItems.bookId").ne(bookId));
		
		Book book = mongoUtils.getBook(bookId);
		
		Update update = new Update();
		update.push("lineItems", new Item(bookId, 0));
		update.inc("subtotal", book.getPrice());;
		
		order = mongoOperation.findAndModify(query, update, 
					new FindAndModifyOptions().returnNew(true), 
					Order.class);
		
		// If item already present then increment quantity
		query = new Query();
		query.addCriteria(Criteria.where("id").is(orderId)
				.and("state").is(Order.State.CART)
				.and("lineItems.bookId").is(bookId));
		 
		update = new Update();
		update.inc("lineItems.$.quantity", 1);
		
		order = mongoOperation.findAndModify(query, update, 
				new FindAndModifyOptions().returnNew(true), 
				Order.class);
			
		return order;
	}

	@Override
	public Order setState(String orderId, Order.State state) {
		
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(orderId));
											
		Update update = new Update();
		update.set("state", state);
											
		Order order = mongoOperation.findAndModify(query, update, Order.class);
		
		return order;
	}
	

	@Override
	public Order getActiveOrder(String userId) {

		ObjectId userObj = new ObjectId(userId);
		
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(userObj)
											.and("state").nin("PRE_SHIPPING", "SHIPPED"));
		
		Order order = mongoOperation.findOne(query, Order.class);
		
		return order;
	}

	@Override
	public Order checkoutOrder(String orderId) {
				
		Query query = new Query();	
		query.addCriteria(Criteria.where("id").is(orderId).and("state").is(Order.State.CART));
				
		Update update = new Update();
		update.set("state", Order.State.PRE_AUTHORIZE);
				
		Order order = mongoOperation.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), Order.class);
		
		return order;		
	}

	
	@Override
	public Order setPreShipping(String orderId) {
		
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(orderId)
								.and("state").is(Order.State.PRE_AUTHORIZE)
							);
												
		Update update = new Update();
		update.set("state", Order.State.PRE_SHIPPING);
		
		Order order = mongoOperation.findAndModify(query, update,
								new FindAndModifyOptions().returnNew(true),
								Order.class);
				
		return order;	
	}
	
	
	@Override
	public Order setCart(String orderId) {
		
		Query query = new Query();	
		query.addCriteria(Criteria.where("id").is(orderId)
								.and("state").is(Order.State.PRE_AUTHORIZE)
							);
												
		Update update = new Update();
		update.set("state", Order.State.CART);
		
		Order order = mongoOperation.findAndModify(query, update,
								new FindAndModifyOptions().returnNew(true),
								Order.class);
	
		return order;	
	}

	@Override
	public Order getOrderById(String orderId) {
		
		return mongoUtils.getOrder(orderId);
	}

	@Override
	public Order editOrder(String orderId, List<Item> items) {
		
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(orderId)
								);
		
		Update update = new Update();
		update.set("lineItems", items);
		
		Order order = mongoOperation.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true),
				Order.class);
		
		// recalculate needed after change
		order = this.recalculateTotal(orderId, false);

		return order;
	}

	@Override
	public Order recalculateTotal(String orderId, boolean payment) {
		
		Order order = mongoUtils.getOrder(orderId);
			
		int subtotal = order.getSubtotal();
		
		/** return updated order or throw a TotalChangedException */
		
		int total = 0;
		
		for (Item item : order.getLineItems()) {
			Book book = mongoUtils.getBook(item.getBookId());
			
			total += book.getPrice() * item.getQuantity();
		}
		
		order.setSubtotal(total);// actual update
		
		// always save updated order
		orderRepository.save(order);
		if (!payment || (total == subtotal)) {
			return order;// always return updated order
		} else {
			throw new TotalChangedException();
		}

	}

	@Override
	public Order setShippingAddress(String orderId, Address address) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(orderId));
		Update update = new Update();
		update.set("shippingAddress", address);
		
		Order order = mongoOperation.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true),
				Order.class);
		
		return order;
	}

	@Override
	public Order setPaymentMethod(String orderId, PaymentMethod method) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(orderId));
		Update update = new Update();
		update.set("paymentMethod", method);
		
		Order order = mongoOperation.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true),
				Order.class);
		
		return order;
	}

	@Override
	public void deleteAll() {
		orderRepository.deleteAll();
	}

	@Override
	public Order setDate(String orderId, Date date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(orderId));
		Update update = new Update();
		update.set("date", date);
		
		Order order = mongoOperation.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true),
				Order.class);
		
		return order;
	}
}