package com.coworking.system.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coworking.system.entity.Booking;
import com.coworking.system.entity.OpenSpace;
import com.coworking.system.entity.PrivateSpace;
import com.coworking.system.entity.User;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    List<Booking> findByOpenSpace(OpenSpace openSpace);
    List<Booking> findByPrivateSpace(PrivateSpace privateSpace);
    List<Booking> findByStatus(Booking.BookingStatus status);
    List<Booking> findByUserId(Long userId);
    
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Booking b WHERE " +
            "(:spaceId = b.openSpace.id AND :isPrivate = false OR " +
            ":spaceId = b.privateSpace.id AND :isPrivate = true) AND " +
            "b.status <> 'CANCELLED' AND " +
            "((b.startTime < :endTime AND b.endTime > :startTime))")
     boolean existsConflictingBookings(
         @Param("spaceId") Long spaceId,
         @Param("isPrivate") Boolean isPrivate,
         @Param("startTime") LocalDateTime startTime,
         @Param("endTime") LocalDateTime endTime
     );
    
    @Query("SELECT b FROM Booking b WHERE " +
            "(:userId IS NULL OR b.user.id = :userId) AND " +
            "((:isPrivate = true AND b.privateSpace.id = :spaceId) OR " +
            "(:isPrivate = false AND b.openSpace.id = :spaceId))")
     List<Booking> findByUserIdAndSpaceId(
         @Param("userId") Long userId,
         @Param("spaceId") Long spaceId,
         @Param("isPrivate") Boolean isPrivate);

     @Query("SELECT b FROM Booking b WHERE b.privateSpace.id = :spaceId")
     List<Booking> findByPrivateSpaceId(@Param("spaceId") Long spaceId);

     @Query("SELECT b FROM Booking b WHERE b.openSpace.id = :spaceId")
     List<Booking> findByOpenSpaceId(@Param("spaceId") Long spaceId);
}