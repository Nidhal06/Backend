package com.coworking.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.coworking.system.entity.PrivateSpace;
import com.coworking.system.entity.Review;
import com.coworking.system.entity.User;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPrivateSpace(PrivateSpace privatespace);
    List<Review> findByUser(User user);   
    @Modifying
    @Query("DELETE FROM Review r WHERE r.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
