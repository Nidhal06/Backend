package com.coworking.system.service;

import com.coworking.system.dto.SubscriptionDto;
import com.coworking.system.dto.SubscriptionResponseDto;
import java.util.List;

public interface SubscriptionService {
    SubscriptionResponseDto create(SubscriptionDto dto);
    SubscriptionResponseDto update(Long id, SubscriptionDto dto);
    void delete(Long id);
    SubscriptionResponseDto getById(Long id);
    List<SubscriptionResponseDto> getAll();
    List<SubscriptionResponseDto> getUserSubscriptions(Long userId);
    SubscriptionResponseDto toggleStatus(Long id);
}