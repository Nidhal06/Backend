package com.coworking.system.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coworking.system.dto.SubscriptionDto;
import com.coworking.system.dto.SubscriptionResponseDto;
import com.coworking.system.entity.Subscription;
import com.coworking.system.entity.User;
import com.coworking.system.exception.ResourceNotFoundException;
import com.coworking.system.repository.SubscriptionRepository;
import com.coworking.system.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository repository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public SubscriptionResponseDto create(SubscriptionDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", dto.getUserId()));
        
        Subscription subscription = modelMapper.map(dto, Subscription.class);
        subscription.setUser(user);
        
        // Calculate end date based on subscription type
        if (dto.getType() == Subscription.SubscriptionType.MONTHLY) {
            subscription.setEndDate(subscription.getStartDate().plusMonths(1));
        } else {
            subscription.setEndDate(subscription.getStartDate().plusYears(1));
        }
        
        Subscription saved = repository.save(subscription);
        return modelMapper.map(saved, SubscriptionResponseDto.class);
    }

    @Override
    @Transactional
    public SubscriptionResponseDto update(Long id, SubscriptionDto dto) {
        Subscription subscription = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));
        
        modelMapper.map(dto, subscription);
        Subscription updated = repository.save(subscription);
        return modelMapper.map(updated, SubscriptionResponseDto.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Subscription subscription = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));
        repository.delete(subscription);
    }

    @Override
    public SubscriptionResponseDto getById(Long id) {
        Subscription subscription = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));
        return modelMapper.map(subscription, SubscriptionResponseDto.class);
    }

    @Override
    public List<SubscriptionResponseDto> getAll() {
        return repository.findAll().stream()
                .map(sub -> modelMapper.map(sub, SubscriptionResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionResponseDto> getUserSubscriptions(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(sub -> modelMapper.map(sub, SubscriptionResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubscriptionResponseDto toggleStatus(Long id) {
        Subscription subscription = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));
        
        subscription.setIsActive(!subscription.getIsActive());
        Subscription updated = repository.save(subscription);
        return modelMapper.map(updated, SubscriptionResponseDto.class);
    }
}