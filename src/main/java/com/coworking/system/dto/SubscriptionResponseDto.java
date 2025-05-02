package com.coworking.system.dto;

import java.time.LocalDateTime;
import com.coworking.system.entity.Subscription.SubscriptionType;
import lombok.Data;

@Data
public class SubscriptionResponseDto {
    private Long id;
    private Long userId;
    private SubscriptionType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double price;

}
