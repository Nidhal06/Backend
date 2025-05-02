package com.coworking.system.service;

import com.coworking.system.dto.PaymentDto;
import com.coworking.system.dto.InvoiceDto;
import java.util.List;

public interface PaymentService {
    PaymentDto createPayment(PaymentDto dto);
    PaymentDto confirmPayment(Long paymentId, Long receptionistId);
    InvoiceDto generateInvoice(Long paymentId);
    List<PaymentDto> getPaymentsByBooking(Long bookingId);
    List<PaymentDto> getUnconfirmedPayments();
    List<PaymentDto> getOverduePayments();
    void sendInvoiceByEmail(Long paymentId, String email);
}