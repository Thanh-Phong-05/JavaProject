package uth.edu.vn.ccmarket.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import uth.edu.vn.ccmarket.model.CarbonCredit;
import uth.edu.vn.ccmarket.model.VerificationRequest;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class AuditReportService {

    public byte[] generateReport(VerificationRequest vr, CarbonCredit credit) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font body  = FontFactory.getFont(FontFactory.HELVETICA, 12);

            doc.add(new Paragraph("CARBON CREDIT VERIFICATION REPORT", title));
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("Verification Request ID: " + vr.getId(), body));
            doc.add(new Paragraph("Credit ID: " + credit.getId(), body));
            doc.add(new Paragraph("Owner ID: " + vr.getOwnerId(), body));
            doc.add(new Paragraph("Status: " + vr.getStatus(), body));
            doc.add(new Paragraph("Created At: " + vr.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME), body));
            if (vr.getDecidedAt() != null) {
                doc.add(new Paragraph("Decided At: " + vr.getDecidedAt().format(DateTimeFormatter.ISO_DATE_TIME), body));
            }
            if (vr.getDecidedBy() != null) {
                doc.add(new Paragraph("Decided By: " + vr.getDecidedBy(), body));
            }
            if (vr.getNote() != null) {
                doc.add(new Paragraph("Note: " + vr.getNote(), body));
            }

            doc.add(new Paragraph(" ", body));
            doc.add(new Paragraph("Verification confirms the validity of carbon reduction data and allocates credits accordingly.", body));

            doc.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate audit PDF", e);
        }
    }
}
