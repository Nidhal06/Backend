package com.coworking.system.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceDownloadController {
    
    private final Path invoiceStorageLocation;

    public InvoiceDownloadController(@Value("${invoice.storage.location}") String invoiceStorageLocation) {
        this.invoiceStorageLocation = Paths.get(invoiceStorageLocation).toAbsolutePath().normalize();
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadInvoice(@PathVariable String fileName) {
        try {
            Path filePath = invoiceStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not download file " + fileName, ex);
        }
    }
}