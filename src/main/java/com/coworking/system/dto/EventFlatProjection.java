package com.coworking.system.dto;

import java.time.LocalDateTime;

public interface EventFlatProjection {
    Long getEventId();
    String getEventTitle();
    String getDescription();
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    Integer getMaxParticipants();
    Double getPrice();
    Boolean getIsActive();  
    Long getPrivateSpaceId();
    String getPrivateSpaceName();
    String getPrivateSpaceDescription();
    Integer getPrivateSpaceCapacity();
    Double getPrivateSpacePricePerHour();
    Double getPrivateSpacePricePerDay();
    Boolean getPrivateSpaceIsActive();
    Long getParticipantId();
    String getParticipantUsername();
    String getParticipantEmail();
    String getParticipantPassword();
    String getParticipantFirstName();
    String getParticipantLastName();
    String getParticipantProfileImagePath();
    String getParticipantPhone();
    String getParticipantType();
    Boolean getParticipantEnabled();
}




