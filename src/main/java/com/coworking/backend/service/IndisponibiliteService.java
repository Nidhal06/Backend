package com.coworking.backend.service;

import com.coworking.backend.dto.IndisponibiliteDTO;
import com.coworking.backend.exception.ResourceNotFoundException;
import com.coworking.backend.model.*;
import com.coworking.backend.repository.IndisponibiliteRepository;
import com.coworking.backend.repository.EspaceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndisponibiliteService {

    private final IndisponibiliteRepository indisponibiliteRepository;
    private final EspaceRepository espaceRepository;
    private final ModelMapper modelMapper;

    public List<IndisponibiliteDTO> getAllIndisponibilites() {
        return indisponibiliteRepository.findAll().stream()
                .map(indisponibilite -> modelMapper.map(indisponibilite, IndisponibiliteDTO.class))
                .collect(Collectors.toList());
    }

    public IndisponibiliteDTO getIndisponibiliteById(Long id) {
        Indisponibilite indisponibilite = indisponibiliteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Indisponibilite not found"));
        return modelMapper.map(indisponibilite, IndisponibiliteDTO.class);
    }

    public IndisponibiliteDTO createIndisponibilite(IndisponibiliteDTO indisponibiliteDTO) {
        Espace espace = espaceRepository.findById(indisponibiliteDTO.getEspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Espace not found"));
        
        Indisponibilite indisponibilite = new Indisponibilite();
        indisponibilite.setEspace(espace);
        indisponibilite.setDateDebut(indisponibiliteDTO.getDateDebut());
        indisponibilite.setDateFin(indisponibiliteDTO.getDateFin());
        indisponibilite.setRaison(indisponibiliteDTO.getRaison());
        
        Indisponibilite savedIndisponibilite = indisponibiliteRepository.save(indisponibilite);
        return modelMapper.map(savedIndisponibilite, IndisponibiliteDTO.class);
    }

    public IndisponibiliteDTO updateIndisponibilite(Long id, IndisponibiliteDTO indisponibiliteDTO) {
        Indisponibilite existingIndisponibilite = indisponibiliteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Indisponibilite not found"));
        
        modelMapper.map(indisponibiliteDTO, existingIndisponibilite);
        Indisponibilite updatedIndisponibilite = indisponibiliteRepository.save(existingIndisponibilite);
        
        return modelMapper.map(updatedIndisponibilite, IndisponibiliteDTO.class);
    }

    public void deleteIndisponibilite(Long id) {
        if (!indisponibiliteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Indisponibilite not found");
        }
        indisponibiliteRepository.deleteById(id);
    }
}