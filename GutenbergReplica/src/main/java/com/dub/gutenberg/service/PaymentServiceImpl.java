package com.dub.gutenberg.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dub.gutenberg.domain.Payment;

@Service
public class PaymentServiceImpl implements PaymentService {

	private static final Logger logger = 
			LoggerFactory.getLogger(PaymentServiceImpl.class);
	
	@Override
	public boolean authorizePayment(Payment payment) {
		logger.warn(Double.toString(payment.getAmount()));
		logger.warn(payment.getCardNumber());
		logger.warn(payment.getCardName());
		
		/** For debugging only */
		if (payment.getCardName().equals("Richard Brunner")) {
			logger.warn("Payment not authorized");
			return false;
		} else {
			return true;
		}
	}

}
