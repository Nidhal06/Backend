package com.coworking.system.service;

import java.util.List;
import com.coworking.system.dto.EventDto;
import com.coworking.system.dto.EventResponseDto;
import com.coworking.system.dto.EventUpdateDto;


public interface AdminEventService {
    EventResponseDto create(EventDto dto);
    EventResponseDto update(Long id, EventUpdateDto dto);
    void delete(Long id);
    EventResponseDto getByIdWithParticipants(Long id);
    List<EventResponseDto> getAll();
    EventResponseDto toggleStatus(Long id);
    EventResponseDto registerUserToEvent(Long eventId, Long userId);
    EventResponseDto unregisterUserFromEvent(Long eventId, Long userId);
}
