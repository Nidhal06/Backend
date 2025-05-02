package com.coworking.system.repository;

import com.coworking.system.entity.Event;
import com.coworking.system.entity.Payment;
import com.coworking.system.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingId(Long bookingId);
    List<Payment> findByIsConfirmedFalse();
    List<Payment> findByIsConfirmedFalseAndPaymentDateBefore(LocalDateTime date);
    
    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE " +
    	       "p.booking.user = :user AND " +
    	       "p.booking.event = :event AND " +
    	       "p.isConfirmed = true")
    	boolean existsByUserAndEventAndConfirmed(
    	    @Param("user") User user,
    	    @Param("event") Event event);
}