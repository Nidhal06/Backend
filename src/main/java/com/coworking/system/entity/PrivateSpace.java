package com.coworking.system.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.BatchSize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "private_spaces")
public class PrivateSpace {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(nullable = false)
    private Integer capacity;
    
    @Column(nullable = false)
    private Double pricePerHour;
    
    @Column(nullable = false)
    private Double pricePerDay;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false)
    private String photo = "";
    
    @ElementCollection
    @CollectionTable(name = "private_space_gallery", joinColumns = @JoinColumn(name = "private_space_id"))
    @Column(name = "image_path")
    private Set<String> gallery; 
    
    @OneToMany(mappedBy = "privateSpace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Booking> bookings;
    
    @OneToMany(mappedBy = "privateSpace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Review> reviews;
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "private_space_amenities",
        joinColumns = @JoinColumn(name = "private_space_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    @BatchSize(size = 100) 
    private Set<Amenity> amenities = new HashSet<>();
    
    @OneToMany(mappedBy = "privateSpace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UnavailabilityPeriod> unavailabilityPeriods;
}