package com.coworking.system.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "unavailability_periods")
public class UnavailabilityPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_space_id")
    private OpenSpace openSpace;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "private_space_id")
    private PrivateSpace privateSpace;
    
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    @Column(nullable = false)
    private LocalDateTime endTime;
    
    @Column(nullable = false)
    private String reason;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @PrePersist
    @PreUpdate
    private void validate() {
        if (openSpace == null && privateSpace == null) {
            throw new IllegalArgumentException("Must be associated with either open space or private space");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }
}