package com.coworking.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.coworking.system.dto.*;
import com.coworking.system.service.ReviewService;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Create a new review")
    @PreAuthorize("hasRole('COWORKER')")
    public ResponseEntity<ReviewResponseDto> createReview(@Valid @RequestBody ReviewDto dto) {
        return ResponseEntity.ok(reviewService.createReview(dto));
    }

    @GetMapping("/space/{spaceId}")
    @Operation(summary = "Get reviews for a space")
    public ResponseEntity<List<ReviewResponseDto>> getSpaceReviews(@PathVariable Long spaceId) {
        return ResponseEntity.ok(reviewService.getReviewsBySpace(spaceId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reviews by user")
    public ResponseEntity<List<ReviewResponseDto>> getUserReviews(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete a review")
    @PreAuthorize("hasRole('COWORKER')")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }
}