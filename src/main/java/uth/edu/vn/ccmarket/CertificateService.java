package uth.edu.vn.ccmarket.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import uth.edu.vn.ccmarket.model.CarbonCredit;
import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.repository.CarbonCreditRepository;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

@Service
public class CertificateService {

    private final CarbonCreditRepository creditRepo;
    private final EVOwnerRepository ownerRepo;

    public CertificateService(CarbonCreditRepository creditRepo, EVOwnerRepository ownerRepo) {
        this.creditRepo = creditRepo;
        this.ownerRepo = ownerRepo;
    }

    public byte[] generateCertificatePdf(Long creditId) {
        CarbonCredit cc = creditRepo.findById(creditId).orElseThrow();
        EVOwner owner = ownerRepo.findById(cc.getOwner().getId()).orElseThrow();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, baos);
            doc.open();
            Font h1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font p  = FontFactory.getFont(FontFactory.HELVETICA, 12);

            doc.add(new Paragraph("CARBON CREDIT CERTIFICATE", h1));
            doc.add(new Paragraph(" ", p));
            doc.add(new Paragraph("Certificate ID: CC-" + cc.getId(), p));
            doc.add(new Paragraph("Owner: " + owner.getUsername() + " (ID: " + owner.getId() + ")", p));
            doc.add(new Paragraph("Quantity (tCO2e): " + cc.getQuantity(), p));
            doc.add(new Paragraph("Verified: " + cc.isVerified(), p));
            doc.add(new Paragraph("Issued Date: " + LocalDate.now(), p));
            doc.add(new Paragraph(" ", p));
            doc.add(new Paragraph("This certificate confirms that the owner holds verified carbon credits", p));
            doc.close();
            return baos.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } finally {
            if (doc.isOpen()) doc.close();
        }
    }
}
