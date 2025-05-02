package com.coworking.system.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewDto {
    @NotNull
    private Long userId;
    
    @NotNull
    private Long privateSpaceId;
    
    @Min(1)
    @Max(5)
    private Integer rating;
    
    private String comment;
}
