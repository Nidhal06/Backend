package com.coworking.system.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InvoiceDto {
    private Long id;
    private Long paymentId;
    private String invoiceNumber;
    private LocalDateTime issueDate;
    private Double amount;
    private String status;
    private String downloadUrl;
}