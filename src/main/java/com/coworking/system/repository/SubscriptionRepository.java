package com.coworking.system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.coworking.system.entity.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
	 List<Subscription> findByUserId(Long userId);

	 @Modifying
	    @Query("DELETE FROM Subscription s WHERE s.user.id = :userId")
	    void deleteAllByUserId(@Param("userId") Long userId);
}
