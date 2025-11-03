package uth.edu.vn.ccmarket.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uth.edu.vn.ccmarket.model.CarbonCredit;
import uth.edu.vn.ccmarket.repository.CarbonCreditRepository;
import uth.edu.vn.ccmarket.service.CertificateService;

import java.util.List;

@Controller
@RequestMapping("/cva")
public class CvaController {

    private final CarbonCreditRepository creditRepo;
    private final CertificateService certificateService;

    public CvaController(CarbonCreditRepository creditRepo, CertificateService certificateService) {
        this.creditRepo = creditRepo;
        this.certificateService = certificateService;
    }

    /** Danh sách pending (chưa verified) để CVA duyệt */
    @GetMapping("/pending")
    public String pending(Model m) {
        List<CarbonCredit> pending = creditRepo.findAll()
                .stream().filter(cc -> !cc.isVerified()).toList();
        m.addAttribute("pendingCredits", pending);
        return "cva_pending";
    }

    /** Duyệt (verify) một credit */
    @PostMapping("/verify")
    public String verify(@RequestParam Long creditId) {
        CarbonCredit cc = creditRepo.findById(creditId).orElseThrow();
        cc.setVerified(true);
        creditRepo.save(cc);
        return "redirect:/cva/pending";
    }

    /** Xuất certificate PDF cho credit đã verified */
    @GetMapping("/certificate/{creditId}.pdf")
    public ResponseEntity<byte[]> certificate(@PathVariable Long creditId) {
        CarbonCredit cc = creditRepo.findById(creditId).orElseThrow();
        if (!cc.isVerified()) {
            return ResponseEntity.badRequest().build();
        }
        byte[] pdf = certificateService.generateCertificatePdf(creditId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate-" + creditId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
