package com.coworking.system.entity;

import java.util.Set;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "open_spaces")
public class OpenSpace {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 
 @Column(nullable = false)
 private String name;
 
 private String description;
 
 @Column(nullable = false)
 private Integer capacity;
 
 @Column(nullable = false)
 private String location;
 
 @Column(nullable = false)
 private Boolean isActive = true;
 
 @OneToMany(mappedBy = "openSpace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
 private Set<Booking> bookings;
 
 @OneToMany(mappedBy = "openSpace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
 private Set<UnavailabilityPeriod> unavailabilityPeriods;


}
