package com.coworking.system.dto;

import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Data
public class PrivateSpaceResponseDto {
	
	private Long id;
    private String name;
    private String description;
    private Integer capacity;
    private String location;
    private Double pricePerHour;
    private Double pricePerDay;
    private Boolean isActive;
    private String photo;
    private Set<String> gallery;
    private Set<AmenityDto> amenities;
    
    // Constructeur par défaut requis par ModelMapper
    public PrivateSpaceResponseDto() {
        this.amenities = new HashSet<>();
        this.gallery = new HashSet<>();
    }
    
    // Votre constructeur existant
    public PrivateSpaceResponseDto(Long id, String name, String description,
            int capacity, String location, double pricePerHour, double pricePerDay,
            boolean isActive, String photo) {
        this(); 
        this.id = id;
        this.name = name;
        this.description = description;
        this.capacity = capacity;
        this.location=location;
        this.pricePerHour = pricePerHour;
        this.pricePerDay = pricePerDay;
        this.isActive = isActive;
        this.photo = photo;
    }
}

