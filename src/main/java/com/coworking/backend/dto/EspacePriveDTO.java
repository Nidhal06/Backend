package com.coworking.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class EspacePriveDTO extends EspaceDTO {
    private double prixParJour;
    private List<String> amenities;
}