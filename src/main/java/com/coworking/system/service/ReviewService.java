package com.coworking.system.service;

import java.util.List;
import com.coworking.system.dto.ReviewDto;
import com.coworking.system.dto.ReviewResponseDto;

public interface ReviewService {
    ReviewResponseDto createReview(ReviewDto dto);
    List<ReviewResponseDto> getReviewsBySpace(Long spaceId);
    List<ReviewResponseDto> getReviewsByUser(Long userId);
    void deleteReview(Long reviewId, Long userId);
}