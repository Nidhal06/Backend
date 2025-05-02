package com.coworking.system.config;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.coworking.system.dto.AmenityDto;
import com.coworking.system.dto.PaymentDto;
import com.coworking.system.dto.PrivateSpaceResponseDto;
import com.coworking.system.entity.Amenity;
import com.coworking.system.entity.Payment;
import com.coworking.system.entity.PrivateSpace;

    
@Configuration
public class ModelMapperConfig {
    
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        modelMapper.getConfiguration()
            .setSkipNullEnabled(true)
            .setCollectionsMergeEnabled(false);
        
        // Amenity mapping
        modelMapper.createTypeMap(Amenity.class, AmenityDto.class)
            .setConverter(context -> new AmenityDto(
                context.getSource().getName(), 
                context.getSource().getDescription()));
        
        // PrivateSpace mapping
        TypeMap<PrivateSpace, PrivateSpaceResponseDto> privateSpaceTypeMap = modelMapper.createTypeMap(PrivateSpace.class, PrivateSpaceResponseDto.class);
        privateSpaceTypeMap.addMappings(mapper -> {
            mapper.map(PrivateSpace::getId, PrivateSpaceResponseDto::setId);
            mapper.map(PrivateSpace::getName, PrivateSpaceResponseDto::setName);
            mapper.map(PrivateSpace::getDescription, PrivateSpaceResponseDto::setDescription);
            mapper.map(PrivateSpace::getCapacity, PrivateSpaceResponseDto::setCapacity);
            mapper.map(PrivateSpace::getLocation, PrivateSpaceResponseDto::setLocation);
            mapper.map(PrivateSpace::getPricePerHour, PrivateSpaceResponseDto::setPricePerHour);
            mapper.map(PrivateSpace::getPricePerDay, PrivateSpaceResponseDto::setPricePerDay);
            mapper.map(PrivateSpace::getIsActive, PrivateSpaceResponseDto::setIsActive);
            mapper.map(PrivateSpace::getPhoto, PrivateSpaceResponseDto::setPhoto);
            mapper.map(PrivateSpace::getGallery, PrivateSpaceResponseDto::setGallery);
            mapper.using(ctx -> mapAmenities(((PrivateSpace) ctx.getSource()).getAmenities()))
                .map(PrivateSpace::getAmenities, PrivateSpaceResponseDto::setAmenities);
        });
        
        // Payment mapping
        TypeMap<Payment, PaymentDto> paymentTypeMap = modelMapper.createTypeMap(Payment.class, PaymentDto.class);
        paymentTypeMap.addMappings(mapper -> {
            mapper.using(ctx -> ((Payment) ctx.getSource()).getBooking() != null ? 
                ((Payment) ctx.getSource()).getBooking().getId() : null)
                .map(Payment::getBooking, PaymentDto::setBookingId);
            mapper.using(ctx -> ((Payment) ctx.getSource()).getConfirmedBy() != null ? 
                ((Payment) ctx.getSource()).getConfirmedBy().getId() : null)
                .map(Payment::getConfirmedBy, PaymentDto::setConfirmedById);
        });
        
        return modelMapper;
    }
    
    private Set<AmenityDto> mapAmenities(Set<Amenity> amenities) {
        if (amenities == null) {
            return new HashSet<>();
        }
        return amenities.stream()
            .map(amenity -> new AmenityDto(amenity.getName(), amenity.getDescription()))
            .collect(Collectors.toSet());
    }
}