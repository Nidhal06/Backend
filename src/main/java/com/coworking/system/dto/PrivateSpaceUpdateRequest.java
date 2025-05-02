package com.coworking.system.dto;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateSpaceUpdateRequest {
	
	    @NotNull
	    private PrivateSpaceDto privateSpace;
	    
	    private MultipartFile photo;
	    private List<MultipartFile> gallery;
	}

