package uth.edu.vn.ccmarket.service;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import uth.edu.vn.ccmarket.model.Transaction;

@Service
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
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("Transaction ID: " + safe(String.valueOf(getValue(tx, "getId"))), body));
            doc.add(new Paragraph("Buyer: " + safe(getPartyUsername(tx, "getBuyer")), body));
            doc.add(new Paragraph("Seller: " + safe(getPartyUsername(tx, "getSeller")), body));
            doc.add(new Paragraph("Credit Amount: " + safe(String.valueOf(getValue(tx, "getCreditAmount"))), body));
            doc.add(new Paragraph("Total Amount: " + safe(String.valueOf(getValue(tx, "getTotalAmount"))), body));

            String completedAt = formatDateTime(getValue(tx, "getCompletedAt"));
            if (completedAt != null) {
                doc.add(new Paragraph("Completed At: " + completedAt, body));
            }

            // listing.credit.verified & id (nếu có)
            Object listing = getValue(tx, "getListing");
            if (listing != null) {
                Object credit = invokeGetter(listing, "getCredit");
                if (credit != null) {
                    Object verified = invokeGetter(credit, "isVerified");
                    Object creditId = invokeGetter(credit, "getId");
                    if (verified != null) doc.add(new Paragraph("Credit Verified: " + verified, body));
                    if (creditId != null) doc.add(new Paragraph("Credit ID: " + creditId, body));
                }
            }

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(
                "This certificate confirms the purchase of carbon credits for emission reduction reporting.", body));

            doc.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate certificate PDF", e);
        }
    }

    // --- helpers ---

    private String getPartyUsername(Object tx, String partyGetter) {
        Object party = getValue(tx, partyGetter);
        if (party == null) return null;

        Object u1 = invokeGetter(party, "getUsername");
        if (u1 != null) return String.valueOf(u1);

        Object email = invokeGetter(party, "getEmail");
        if (email != null) return String.valueOf(email);

        return String.valueOf(party);
    }

    private Object getValue(Object target, String method) {
        try {
            Method m = target.getClass().getMethod(method);
            m.setAccessible(true);
            return m.invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Object invokeGetter(Object target, String method) {
        try {
            Method m = target.getClass().getMethod(method);
            m.setAccessible(true);
            return m.invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String formatDateTime(Object v) {
        if (v == null) return null;
        try {
            if (v instanceof TemporalAccessor) {
                return DateTimeFormatter.ISO_DATE_TIME.format((TemporalAccessor) v);
            }
            if (v instanceof Date) {
                // Date -> ISO-like
                return ((Date) v).toInstant().toString();
            }
            return String.valueOf(v);
        } catch (Exception e) {
            return String.valueOf(v);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
