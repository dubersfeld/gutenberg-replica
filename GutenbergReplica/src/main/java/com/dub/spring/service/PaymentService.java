package com.dub.spring.service;

import org.springframework.security.access.prepost.PreAuthorize;

import com.dub.spring.domain.Payment;

@PreAuthorize("isFullyAuthenticated()")
public interface PaymentService {

	public boolean authorizePayment(Payment payment); 
}
