package com.dub.gutenberg.service;

import org.springframework.security.access.prepost.PreAuthorize;

import com.dub.gutenberg.domain.Payment;

@PreAuthorize("isFullyAuthenticated()")
public interface PaymentService {

	public boolean authorizePayment(Payment payment); 
}
