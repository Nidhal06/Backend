package com.coworking.system.dto;

import lombok.Data;

import java.time.LocalDateTime;

import com.coworking.system.entity.Payment.PaymentMethod;

@Data
public class PaymentDto {
	 private Long id;
	    private Long bookingId;
	    private Long confirmedById;  
	    private Double amount;
	    private LocalDateTime paymentDate;
	    private PaymentMethod method;
	    private Boolean isConfirmed;
	    private String transactionId;
}