package com.coworking.system.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coworking.system.dto.*;
import com.coworking.system.entity.*;
import com.coworking.system.exception.BadRequestException;
import com.coworking.system.exception.ResourceNotFoundException;
import com.coworking.system.repository.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final Path invoiceStorageLocation;
    private final EmailService emailService;

    @Override
    @Transactional
    public PaymentDto createPayment(PaymentDto dto) {
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", dto.getBookingId()));
        
        Payment payment = modelMapper.map(dto, Payment.class);
        payment.setBooking(booking);
        payment.setPaymentDate(LocalDateTime.now());
        
        Payment saved = paymentRepository.save(payment);
        return modelMapper.map(saved, PaymentDto.class);
    }

    @Override
    @Transactional
    public PaymentDto confirmPayment(Long paymentId, Long receptionistId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
        
        User receptionist = userRepository.findById(receptionistId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", receptionistId));
        
        payment.setIsConfirmed(true);
        payment.setConfirmedBy(receptionist);
        
        Payment updated = paymentRepository.save(payment);
        return modelMapper.map(updated, PaymentDto.class);
    }


    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(invoiceStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize invoice storage", e);
        }
    }

    @Override
    @Transactional
    public InvoiceDto generateInvoice(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
        
        if (!payment.getIsConfirmed()) {
            throw new BadRequestException("Cannot generate invoice for unconfirmed payment");
        }
        
        // Créer le nom de fichier unique
        String invoiceNumber = "INV-" + System.currentTimeMillis();
        String fileName = invoiceNumber + ".pdf";
        Path invoicePath = invoiceStorageLocation.resolve(fileName);
        
        try {
            // Ici vous généreriez le vrai PDF avec une bibliothèque comme iText
            byte[] invoiceContent = generatePdfContent(payment); 
            Files.write(invoicePath, invoiceContent);
            
            InvoiceDto invoice = new InvoiceDto();
            invoice.setPaymentId(paymentId);
            invoice.setAmount(payment.getAmount());
            invoice.setIssueDate(LocalDateTime.now());
            invoice.setInvoiceNumber(invoiceNumber);
            invoice.setStatus("GENERATED");
            invoice.setDownloadUrl("/api/invoices/download/" + fileName);
            
            return invoice;
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate invoice", e);
        }
    }

    private byte[] generatePdfContent(Payment payment) {
    	try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            // Couleurs modernes 
            Color primaryColor = new Color(0, 56, 147); 
            Color secondaryColor = new Color(200, 16, 46);
            
            // En-tête
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, primaryColor);
            Paragraph header = new Paragraph("FACTURE", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            // 3. Informations de la société
            Font companyFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph companyInfo = new Paragraph();
            companyInfo.add(new Chunk("Coworking Space\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            companyInfo.add(new Chunk("Boulevard cheikh zaid, Tunis, Tunisia\n", companyFont));
            companyInfo.add(new Chunk("7000 Tunis, Tunisie\n", companyFont));
            companyInfo.add(new Chunk("Tél: +216 70 037 520\n", companyFont));
            companyInfo.add(new Chunk("Email: level1hub1@gmail.com\n\n", companyFont));
            document.add(companyInfo);

            // 4. Informations du client
            Font clientFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph clientInfo = new Paragraph();
            clientInfo.add(new Chunk("Facturé à:\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            clientInfo.add(new Chunk(payment.getBooking().getUser().getFirstName() + " " + 
                                   payment.getBooking().getUser().getLastName() + "\n", clientFont));
            clientInfo.add(new Chunk(payment.getBooking().getUser().getEmail() + "\n", clientFont));
            clientInfo.add(new Chunk(payment.getBooking().getUser().getPhone() + "\n\n", clientFont));
            document.add(clientInfo);

            // 5. Détails de la facture
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // En-têtes du tableau
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            table.addCell(new Phrase("Description", tableHeaderFont));
            table.addCell(new Phrase("Date", tableHeaderFont));
            table.addCell(new Phrase("Durée", tableHeaderFont));
            table.addCell(new Phrase("Montant", tableHeaderFont));

            // Contenu du tableau
            Font tableContentFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            
            String spaceType = payment.getBooking().getPrivateSpace() != null ? 
                              "Espace Privé" : "Espace Ouvert";
            String spaceName = payment.getBooking().getPrivateSpace() != null ? 
                              payment.getBooking().getPrivateSpace().getName() : 
                              payment.getBooking().getOpenSpace().getName();
            
            table.addCell(new Phrase(spaceType + ": " + spaceName, tableContentFont));
            table.addCell(new Phrase(payment.getBooking().getStartTime().toLocalDate().toString(), tableContentFont));
            
            long hours = Duration.between(
                payment.getBooking().getStartTime(), 
                payment.getBooking().getEndTime()).toHours();
            table.addCell(new Phrase(hours + " heures", tableContentFont));
            
            table.addCell(new Phrase(String.format("%.2f €", payment.getAmount()), tableContentFont));
            
            document.add(table);

            // 6. Total et mentions légales
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Paragraph total = new Paragraph();
            total.add(new Chunk("\nTotal TTC: ", totalFont));
            total.add(new Chunk(String.format("%.2f €", payment.getAmount()), totalFont));
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8);
            Paragraph footer = new Paragraph("\n\nMerci pour votre confiance.\n" +
                                           "TVA non applicable, art. 293 B du CGI", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return baos.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    @Override
    public List<PaymentDto> getPaymentsByBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId).stream()
                .map(payment -> modelMapper.map(payment, PaymentDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> getUnconfirmedPayments() {
        return paymentRepository.findByIsConfirmedFalse().stream()
                .map(payment -> modelMapper.map(payment, PaymentDto.class))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void sendInvoiceByEmail(Long paymentId, String email) {
        InvoiceDto invoice = generateInvoice(paymentId);
        Path invoicePath = invoiceStorageLocation.resolve(invoice.getInvoiceNumber() + ".pdf");
        
        try {
            emailService.sendMessageWithAttachment(
                email,
                "Your Invoice #" + invoice.getInvoiceNumber(),
                "Please find your invoice attached.",
                invoicePath
            );
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

 
    @Override
    public List<PaymentDto> getOverduePayments() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(3);
        return paymentRepository.findByIsConfirmedFalseAndPaymentDateBefore(threshold).stream()
                .map(payment -> modelMapper.map(payment, PaymentDto.class))
                .collect(Collectors.toList());
    }
}