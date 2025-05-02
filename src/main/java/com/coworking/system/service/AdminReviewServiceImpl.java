package com.coworking.system.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.coworking.system.dto.ReviewResponseDto;
import com.coworking.system.repository.ReviewRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminReviewServiceImpl implements AdminReviewService {
    private final ReviewRepository repository;
    private final ModelMapper modelMapper;


    @Override
    public List<ReviewResponseDto> getAllReviews() {
        return repository.findAll().stream()
                .map(review -> modelMapper.map(review, ReviewResponseDto.class))
                .collect(Collectors.toList());
    }
}
