package com.coworking.system.entity;


import lombok.Data;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Data
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "private_space_id", nullable = false)
    private PrivateSpace privateSpace;
    
    @Column(nullable = false)
    private Integer rating;
    
    private String comment;
    
    @Column(nullable = false)
    private LocalDateTime reviewDate = LocalDateTime.now();
    
}
