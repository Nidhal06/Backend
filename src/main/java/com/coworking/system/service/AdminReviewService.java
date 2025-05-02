package com.coworking.system.service;

import java.util.List;
import com.coworking.system.dto.ReviewResponseDto;


public interface AdminReviewService {
    List<ReviewResponseDto> getAllReviews();
}
