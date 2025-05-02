package com.coworking.system.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "amenities")
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    private String description;
}
