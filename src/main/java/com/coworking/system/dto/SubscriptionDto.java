package com.coworking.system.dto;

import java.time.LocalDateTime;
import com.coworking.system.entity.Subscription.SubscriptionType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SubscriptionDto {
    private Long id;
    
    @NotNull
    private Long userId;
    
    @NotNull
    private SubscriptionType type;
    
    @NotNull
    @FutureOrPresent
    private LocalDateTime startDate;
    
    @NotNull
    @Future
    private LocalDateTime endDate;
    
    @NotNull
    @Positive
    private Double price;
}
