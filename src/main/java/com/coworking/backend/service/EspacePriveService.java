package com.coworking.backend.service;

import com.coworking.backend.dto.EspacePriveDTO;
import com.coworking.backend.exception.ResourceNotFoundException;
import com.coworking.backend.model.EspacePrive;
import com.coworking.backend.repository.EspacePriveRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspacePriveService {

    private final EspacePriveRepository espacePriveRepository;
    private final ModelMapper modelMapper;

    public List<EspacePriveDTO> getAllEspacePrives() {
        return espacePriveRepository.findAll().stream()
                .map(espace -> modelMapper.map(espace, EspacePriveDTO.class))
                .collect(Collectors.toList());
    }

    public EspacePriveDTO getEspacePriveById(Long id) {
        EspacePrive espacePrive = espacePriveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EspacePrive not found"));
        return modelMapper.map(espacePrive, EspacePriveDTO.class);
    }

    public EspacePriveDTO createEspacePrive(EspacePriveDTO espacePriveDTO) {
        EspacePrive espacePrive = modelMapper.map(espacePriveDTO, EspacePrive.class);
        EspacePrive savedEspacePrive = espacePriveRepository.save(espacePrive);
        return modelMapper.map(savedEspacePrive, EspacePriveDTO.class);
    }

    public EspacePriveDTO updateEspacePrive(Long id, EspacePriveDTO espacePriveDTO) {
        EspacePrive existingEspacePrive = espacePriveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EspacePrive not found"));
        
        // Ensure the DTO has the correct ID
        espacePriveDTO.setId(id);
        
        // Map non-null properties from DTO to existing entity
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(espacePriveDTO, existingEspacePrive);
        
        EspacePrive updatedEspacePrive = espacePriveRepository.save(existingEspacePrive);
        
        return modelMapper.map(updatedEspacePrive, EspacePriveDTO.class);
    }

    public void deleteEspacePrive(Long id) {
        if (!espacePriveRepository.existsById(id)) {
            throw new ResourceNotFoundException("EspacePrive not found");
        }
        espacePriveRepository.deleteById(id);
    }
}