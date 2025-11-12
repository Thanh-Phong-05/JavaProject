package uth.edu.vn.ccmarket.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uth.edu.vn.ccmarket.model.CarbonCredit;
import uth.edu.vn.ccmarket.model.VerificationRequest;
import uth.edu.vn.ccmarket.repository.CarbonCreditRepository;
import uth.edu.vn.ccmarket.repository.VerificationRequestRepository;
import uth.edu.vn.ccmarket.service.AuditReportService;
import uth.edu.vn.ccmarket.service.VerificationService;

@Controller
@RequestMapping("/cva")
public class CvaController {

    private final VerificationService verificationService;
    private final VerificationRequestRepository requestRepo;
    private final CarbonCreditRepository creditRepo;
    private final AuditReportService auditReportService;

    public CvaController(VerificationService verificationService,
                         VerificationRequestRepository requestRepo,
                         CarbonCreditRepository creditRepo,
                         AuditReportService auditReportService) {
        this.verificationService = verificationService;
        this.requestRepo = requestRepo;
        this.creditRepo = creditRepo;
        this.auditReportService = auditReportService;
    }

    /** Trang danh sách yêu cầu kiểm định (CVA dashboard) */
    @GetMapping("/requests")
    public String listRequests(@RequestParam(value = "status", required = false) String status,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "20") int size,
                               org.springframework.ui.Model model) {
        VerificationRequest.Status st = null;
        if (status != null && !status.isBlank()) {
            st = VerificationRequest.Status.valueOf(status.toUpperCase());
        }
        Page<VerificationRequest> result = verificationService.listByStatus(st, page, size);
        model.addAttribute("requests", result.getContent());
        model.addAttribute("status", status == null ? "ALL" : status);
        return "cva-dashboard"; // trang HTML
    }

    /** Duyệt tín chỉ carbon */
    @PostMapping("/requests/{id}/approve")
    public String approve(@PathVariable Long id,
                          @RequestParam(value = "note", required = false) String note,
                          Principal principal,
                          org.springframework.web.servlet.mvc.support.RedirectAttributes redirect) {
        try {
            String decidedBy = principal != null ? principal.getName() : "cva";
            verificationService.approve(id, decidedBy, note);
            redirect.addFlashAttribute("message", "✅ Duyệt yêu cầu #" + id + " thành công!");
        } catch (Exception e) {
            redirect.addFlashAttribute("message", "❌ Lỗi: " + e.getMessage());
        }
        return "redirect:/cva/requests";
    }

    /** Từ chối yêu cầu kiểm định */
    @PostMapping("/requests/{id}/reject")
    public String reject(@PathVariable Long id,
                         @RequestParam(value = "note", required = false) String note,
                         Principal principal,
                         org.springframework.web.servlet.mvc.support.RedirectAttributes redirect) {
        try {
            String decidedBy = principal != null ? principal.getName() : "cva";
            verificationService.reject(id, decidedBy, note);
            redirect.addFlashAttribute("message", "❌ Từ chối yêu cầu #" + id + " thành công!");
        } catch (Exception e) {
            redirect.addFlashAttribute("message", "❌ Lỗi: " + e.getMessage());
        }
        return "redirect:/cva/requests";
    }

    /** Xuất file PDF biên bản kiểm định */
    @GetMapping("/requests/{id}/report")
    public ResponseEntity<byte[]> report(@PathVariable Long id) {
        VerificationRequest vr = requestRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu #" + id));
        CarbonCredit credit = creditRepo.findById(vr.getCreditId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tín chỉ #" + vr.getCreditId()));

        byte[] pdf = auditReportService.generateReport(vr, credit);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=verification_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
