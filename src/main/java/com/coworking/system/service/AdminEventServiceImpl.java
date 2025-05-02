package com.coworking.system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coworking.system.dto.EventDto;
import com.coworking.system.dto.EventFlatProjection;
import com.coworking.system.dto.EventResponseDto;
import com.coworking.system.dto.EventUpdateDto;
import com.coworking.system.dto.PrivateSpaceDto;
import com.coworking.system.dto.UserDto;
import com.coworking.system.entity.Event;
import com.coworking.system.entity.PrivateSpace;
import com.coworking.system.entity.User;
import com.coworking.system.exception.BadRequestException;
import com.coworking.system.exception.ResourceNotFoundException;
import com.coworking.system.repository.EventRepository;
import com.coworking.system.repository.PaymentRepository;
import com.coworking.system.repository.PrivateSpaceRepository;
import com.coworking.system.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) 
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository repository;
    private final PrivateSpaceRepository spaceRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    
    @Transactional
    public EventResponseDto create(EventDto dto) {
        try {
            log.info("Creating event with data: {}", dto);
            
            if (dto.getEndTime().isBefore(dto.getStartTime())) {
                throw new BadRequestException("End time must be after start time");
            }

            // Vérifier si l'espace existe
            PrivateSpace space = spaceRepository.findById(dto.getPrivateSpaceId())
                    .orElseThrow(() -> {
                        log.error("Private Space not found with id: {}", dto.getPrivateSpaceId());
                        return new ResourceNotFoundException("PrivateSpace", "id", dto.getPrivateSpaceId());
                    });
            
            log.info("Found space: {}", space.getName());
            
            // Créer une nouvelle instance plutôt que d'utiliser ModelMapper directement
            Event event = new Event();
            event.setTitle(dto.getTitle());
            event.setDescription(dto.getDescription());
            event.setStartTime(dto.getStartTime());
            event.setEndTime(dto.getEndTime());
            event.setPrivateSpace(space);
            event.setMaxParticipants(dto.getMaxParticipants());
            event.setPrice(dto.getPrice());
            event.setIsActive(true);
            
            Event saved = repository.save(event);
            log.info("Event created successfully with id: {}", saved.getId());
            
            return modelMapper.map(saved, EventResponseDto.class);
        } catch (Exception e) {
            log.error("Error creating event", e);
            throw new RuntimeException("Failed to create event: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public EventResponseDto update(Long id, EventUpdateDto dto) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
        
        // Only update fields that are not null in the DTO
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getStartTime() != null) {
            event.setStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            event.setEndTime(dto.getEndTime());
        }
        if (dto.getPrivateSpaceId() != null) {
        	PrivateSpace space = spaceRepository.findById(dto.getPrivateSpaceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Space", "id", dto.getPrivateSpaceId()));
            event.setPrivateSpace(space);
        }
        if (dto.getMaxParticipants() != null) {
            event.setMaxParticipants(dto.getMaxParticipants());
        }
        if (dto.getPrice() != null) {
            event.setPrice(dto.getPrice());
        }
        
        Event updated = repository.save(event);
        return modelMapper.map(updated, EventResponseDto.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
        repository.delete(event);
    }

    @Override
    public EventResponseDto getByIdWithParticipants(Long id) {
        Event event = repository.findByIdWithSpace(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
        
        EventResponseDto dto = modelMapper.map(event, EventResponseDto.class);
        
        Set<User> participants = repository.findParticipantsByEventId(id);
        Set<UserDto> participantDtos = participants.stream()
            .map(user -> modelMapper.map(user, UserDto.class))
            .collect(Collectors.toSet());
        dto.setParticipants(participantDtos);
        
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getAll() {
        List<EventFlatProjection> flatList = repository.findAllEventsWithParticipants();
        return buildEventDtoList(flatList);
    }

    private List<EventResponseDto> buildEventDtoList(List<EventFlatProjection> flatList) {
        Map<Long, EventResponseDto> dtoMap = new LinkedHashMap<>();

        for (EventFlatProjection row : flatList) {
            Long eventId = row.getEventId();

            EventResponseDto dto = dtoMap.computeIfAbsent(eventId, id -> {
                EventResponseDto d = new EventResponseDto();
                d.setId(eventId);
                d.setTitle(row.getEventTitle());
                d.setDescription(row.getDescription());
                d.setStartTime(row.getStartTime());
                d.setEndTime(row.getEndTime());
                d.setMaxParticipants(row.getMaxParticipants());
                d.setPrice(row.getPrice());
                d.setIsActive(row.getIsActive());
                d.setParticipants(new HashSet<>());

                if (row.getPrivateSpaceId() != null) {
                	PrivateSpaceDto spaceDto = new PrivateSpaceDto();
                    spaceDto.setId(row.getPrivateSpaceId());
                    spaceDto.setName(row.getPrivateSpaceName());
                    spaceDto.setDescription(row.getPrivateSpaceDescription());
                    spaceDto.setCapacity(row.getPrivateSpaceCapacity());
                    spaceDto.setPricePerHour(row.getPrivateSpacePricePerHour());
                    spaceDto.setPricePerDay(row.getPrivateSpacePricePerDay());
                    spaceDto.setIsActive(row.getPrivateSpaceIsActive());
                    d.setPrivateSpace(spaceDto);
                }

                return d;
            });

            if (row.getParticipantId() != null) {
                UserDto user = new UserDto();
                user.setId(row.getParticipantId());
                user.setUsername(row.getParticipantUsername());
                user.setEmail(row.getParticipantEmail());
                user.setPassword(row.getParticipantPassword());
                user.setFirstName(row.getParticipantFirstName());
                user.setLastName(row.getParticipantLastName());
                user.setProfileImagePath(row.getParticipantProfileImagePath());
                user.setPhone(row.getParticipantPhone());
                user.setType(row.getParticipantType());
                user.setEnabled(row.getParticipantEnabled());
                dto.getParticipants().add(user);
            }
        }

        return new ArrayList<>(dtoMap.values());
    }


    @Override
    @Transactional
    public EventResponseDto toggleStatus(Long id) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
        
        event.setIsActive(!event.getIsActive());
        Event updated = repository.save(event);
        return modelMapper.map(updated, EventResponseDto.class);
    }
    

    @Override
    @Transactional
    public EventResponseDto registerUserToEvent(Long eventId, Long userId) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Vérifier si l'utilisateur est déjà inscrit
        if (repository.existsByIdAndParticipantsId(eventId, userId)) {
            throw new BadRequestException("User is already registered to this event");
        }
        
        // Vérifier si l'événement est complet
        int participantCount = repository.countParticipantsByEventId(eventId);
        if (participantCount >= event.getMaxParticipants()) {
            throw new BadRequestException("Event is already full");
        }
        
        // Vérifier que l'utilisateur a payé si l'événement est payant
        if (event.getPrice() > 0) {
            boolean hasPaid = paymentRepository.existsByUserAndEventAndConfirmed(user, event);
            if (!hasPaid) {
                throw new BadRequestException("Payment is required to join this event");
            }
        }
        
        event.getParticipants().add(user);
        Event updated = repository.save(event);       

        return modelMapper.map(updated, EventResponseDto.class);
    }
    
    
    @Override
    @Transactional
    public EventResponseDto unregisterUserFromEvent(Long eventId, Long userId) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Vérifier si l'utilisateur est bien inscrit
        if (!repository.existsByIdAndParticipantsId(eventId, userId)) {
            throw new BadRequestException("User is not registered to this event");
        }
        
        event.getParticipants().removeIf(p -> p.getId().equals(userId));
        Event updated = repository.save(event);
        return modelMapper.map(updated, EventResponseDto.class);
    }
}
