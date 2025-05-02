package com.coworking.system.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponseDto {
    private Long id;
    private UserDto user;
    private PrivateSpaceDto privateSpace;
    private Integer rating;
    private String comment;
    private LocalDateTime reviewDate;
}
