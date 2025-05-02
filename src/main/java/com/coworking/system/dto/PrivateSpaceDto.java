package com.coworking.system.dto;

import lombok.Data;
import java.util.Set;
import jakarta.validation.constraints.*;

@Data
public class PrivateSpaceDto {
	
private Long id;
	
    @NotBlank
    private String name;
    
    private String description;
    
    @NotNull
    @Positive
    private Integer capacity;
    
    @NotBlank
    private String location;
    
    @NotNull
    @Positive
    private Double pricePerHour;
    
    @NotNull
    @Positive
    private Double pricePerDay;
    
    private Set<Long> amenityIds;
    
    private Boolean isActive ;

}
