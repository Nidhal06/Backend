package com.coworking.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.coworking.system.dto.InvoiceDto;
import com.coworking.system.dto.PaymentDto;
import com.coworking.system.service.PaymentService;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {
    private final PaymentService service;

    @PostMapping
    @Operation(summary = "Create a payment")
    @PreAuthorize("hasRole('RECEPTIONIST')") 
    public ResponseEntity<PaymentDto> create(@Valid @RequestBody PaymentDto dto) {
        return ResponseEntity.ok(service.createPayment(dto));
    }

    @PostMapping("/{paymentId}/confirm")
    @Operation(summary = "Confirm a payment")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<PaymentDto> confirm(
            @PathVariable Long paymentId,
            @RequestParam Long receptionistId) {
        return ResponseEntity.ok(service.confirmPayment(paymentId, receptionistId));
    }

    @PostMapping("/{paymentId}/invoice")
    @Operation(summary = "Generate invoice for payment")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<InvoiceDto> generateInvoice(@PathVariable Long paymentId) {
        return ResponseEntity.ok(service.generateInvoice(paymentId));
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get payments for booking")
    public ResponseEntity<List<PaymentDto>> getByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(service.getPaymentsByBooking(bookingId));
    }

    @GetMapping("/unconfirmed")
    @Operation(summary = "Get unconfirmed payments")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<List<PaymentDto>> getUnconfirmed() {
        return ResponseEntity.ok(service.getUnconfirmedPayments());
    }
}