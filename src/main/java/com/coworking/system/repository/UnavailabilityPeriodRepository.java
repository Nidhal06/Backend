package com.coworking.system.repository;

import com.coworking.system.entity.UnavailabilityPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UnavailabilityPeriodRepository extends JpaRepository<UnavailabilityPeriod, Long> {
    List<UnavailabilityPeriod> findByOpenSpaceId(Long openSpaceId);
    List<UnavailabilityPeriod> findByPrivateSpaceId(Long privateSpaceId);
    
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM UnavailabilityPeriod u WHERE " +
            "u.openSpace.id = :spaceId AND " +
            "((u.startTime < :endTime AND u.endTime > :startTime))")
     boolean existsForOpenSpaceDuringPeriod(
         @Param("spaceId") Long spaceId,
         @Param("startTime") LocalDateTime startTime,
         @Param("endTime") LocalDateTime endTime
     );
     
     @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM UnavailabilityPeriod u WHERE " +
            "u.privateSpace.id = :spaceId AND " +
            "((u.startTime < :endTime AND u.endTime > :startTime))")
     boolean existsForPrivateSpaceDuringPeriod(
         @Param("spaceId") Long spaceId,
         @Param("startTime") LocalDateTime startTime,
         @Param("endTime") LocalDateTime endTime
     );
}