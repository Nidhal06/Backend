package com.coworking.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.coworking.system.dto.EventFlatProjection;
import com.coworking.system.entity.Event;
import com.coworking.system.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByIsActiveTrueAndStartTimeAfter(LocalDateTime date);
    List<Event> findByParticipantsId(Long participantId);
    
    @Query("SELECT COUNT(p) FROM Event e JOIN e.participants p WHERE e.id = :eventId")
    int countParticipantsByEventId(@Param("eventId") Long eventId);
    
    boolean existsByIdAndParticipantsId(Long eventId, Long userId);
    
    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.privateSpace LEFT JOIN FETCH e.participants")
    List<Event> findAllWithParticipantsAndPrivateSpace();
     
    @Query("SELECT p FROM Event e JOIN e.participants p WHERE e.id = :eventId")
    Set<User> findParticipantsByEventId(@Param("eventId") Long eventId);
     
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.privateSpace WHERE e.id = :id")
    Optional<Event> findByIdWithPrivateSpace(@Param("id") Long id);
    
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.privateSpace")
    List<Event> findAllWithSpace();

    @Query("SELECT e FROM Event e JOIN FETCH e.privateSpace WHERE e.id = :id")
    Optional<Event> findByIdWithSpace(@Param("id") Long id);

    
    //find all event 
    @Query(value = """
            SELECT 
                e.id AS eventId,
                e.title AS eventTitle,
                e.description,
                e.start_time AS startTime,
                e.end_time AS endTime,
                e.max_participants As maxparticipants,
                e.price As price,
                e.is_active As IsActive,
                s.id AS spaceId,
                s.name AS privatespaceName,
                s.description AS privatespaceDescription,
                s.capacity AS privatespaceCapacity,
                s.price_per_hour AS privatespacePricePerHour,
                s.price_per_day AS privatespacePricePerDay,
                s.is_active AS privatespaceIsActive,
                u.id AS participantId,
                u.username AS participantUsername,
                u.email AS participantEmail,
                u.password AS participantPassword,
                u.first_name AS participantFirstName,
                u.last_name AS participantLastName,
                u.profile_image_path AS participantProfileImagePath,
                u.phone AS participantPhone,
                u.type AS participantType,
                u.enabled AS participantEnabled
            FROM events e
            LEFT JOIN privatespaces s ON e.privatespace_id = s.id
            LEFT JOIN event_participants ep ON e.id = ep.event_id
            LEFT JOIN users u ON ep.user_id = u.id
            ORDER BY e.id, u.id
            """, nativeQuery = true)
    List<EventFlatProjection> findAllEventsWithParticipants();

}