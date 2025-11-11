package uth.edu.vn.ccmarket.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uth.edu.vn.ccmarket.domain.Transaction;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CertificateService {

    public byte[] generateCertificate(Transaction tx) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font body  = FontFactory.getFont(FontFactory.HELVETICA, 12);

            doc.add(new Paragraph("CARBON CREDIT CERTIFICATE", title));
            doc.add(new Paragraph(" ", body));

            doc.add(new Paragraph("Transaction ID: " + tx.getId(), body));
            doc.add(new Paragraph("Buyer: " + safe(tx.getBuyer() != null ? tx.getBuyer().getUsername() : ""), body));
            doc.add(new Paragraph("Seller: " + safe(tx.getSeller() != null ? tx.getSeller().getUsername() : ""), body));
            doc.add(new Paragraph("Credit Amount (credits): " + tx.getCreditAmount(), body));
            doc.add(new Paragraph("Total Amount: " + tx.getTotalAmount(), body));
            if (tx.getCompletedAt() != null) {
                doc.add(new Paragraph("Completed At: " + tx.getCompletedAt().format(DateTimeFormatter.ISO_DATE_TIME), body));
            }
            if (tx.getListing() != null && tx.getListing().getCredit() != null) {
                doc.add(new Paragraph("Credit Verified: " + tx.getListing().getCredit().isVerified(), body));
                doc.add(new Paragraph("Credit ID: " + tx.getListing().getCredit().getId(), body));
            }

            doc.add(new Paragraph(" ", body));
            doc.add(new Paragraph("This certificate confirms the purchase of carbon credits for emission reduction reporting.", body));

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate certificate PDF", e);
        }
    }

    private String safe(String s) { return s == null ? "" : s; }
}
