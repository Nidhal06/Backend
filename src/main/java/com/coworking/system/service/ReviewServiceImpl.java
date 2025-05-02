package com.coworking.system.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coworking.system.dto.*;
import com.coworking.system.entity.*;
import com.coworking.system.exception.*;
import com.coworking.system.repository.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PrivateSpaceRepository privateSpaceRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ReviewResponseDto createReview(ReviewDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", dto.getUserId()));
        
        PrivateSpace privateSpace = privateSpaceRepository.findById(dto.getPrivateSpaceId())
                .orElseThrow(() -> new ResourceNotFoundException("PrivateSpace", "id", dto.getPrivateSpaceId()));

        // Vérifier si l'utilisateur a bien réservé cet espace
        if (!hasUserBookedSpace(user, privateSpace)) {
            throw new BadRequestException("You can only review spaces you have booked");
        }

        Review review = new Review();
        review.setUser(user);
        review.setPrivateSpace(privateSpace);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setReviewDate(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);
        return modelMapper.map(savedReview, ReviewResponseDto.class);
    }

    private boolean hasUserBookedSpace(User user, PrivateSpace privateSpace) {
        // Implémenter la vérification que l'utilisateur a bien réservé cet espace
        return true; // Temporaire
    }

    @Override
    public List<ReviewResponseDto> getReviewsBySpace(Long spaceId) {
        PrivateSpace space = privateSpaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("PrivateSpace", "id", spaceId));
        
        return reviewRepository.findByPrivateSpace(space).stream()
                .map(review -> modelMapper.map(review, ReviewResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponseDto> getReviewsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return reviewRepository.findByUser(user).stream()
                .map(review -> modelMapper.map(review, ReviewResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        if (!review.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only delete your own reviews");
        }
        
        reviewRepository.delete(review);
    }
}