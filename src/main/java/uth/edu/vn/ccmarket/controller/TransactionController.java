package uth.edu.vn.ccmarket.controller;

import java.lang.reflect.Method;
import java.security.Principal;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uth.edu.vn.ccmarket.model.CCBuyer;
import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.repository.CCBuyerRepository;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import uth.edu.vn.ccmarket.repository.TransactionRepository;
import uth.edu.vn.ccmarket.service.CertificateService;
import uth.edu.vn.ccmarket.service.TransactionService;

@Controller
public class TransactionController {

    private final TransactionService transactionService;
    private final EVOwnerRepository ownerRepo;
    private final CCBuyerRepository buyerRepo;

    private final TransactionRepository transactionRepository;
    private final CertificateService certificateService;

    public TransactionController(TransactionService transactionService,
                                 EVOwnerRepository ownerRepo,
                                 CCBuyerRepository buyerRepo,
                                 TransactionRepository transactionRepository,
                                 CertificateService certificateService) {
        this.transactionService = transactionService;
        this.ownerRepo = ownerRepo;
        this.buyerRepo = buyerRepo;
        this.transactionRepository = transactionRepository;
        this.certificateService = certificateService;
    }

    @PostMapping("/buy")
    public String executeBuyTransaction(
            @RequestParam("listingId") Long listingId,
            @RequestParam("quantity") double quantity,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();

        try {
            var buyerOpt = buyerRepo.findByUsername(username);
            if (buyerOpt.isPresent()) {
                CCBuyer buyer = buyerOpt.get();
                transactionService.executeBuyTransactionAsBuyer(listingId, quantity, buyer);
                redirectAttributes.addFlashAttribute("message", "Giao dịch thành công!");
                return "redirect:/marketplace";
            }

            var ownerOpt = ownerRepo.findByUsername(username);
            if (ownerOpt.isPresent()) {
                EVOwner ownerAsBuyer = ownerOpt.get();
                transactionService.executeBuyTransactionAsOwner(listingId, quantity, ownerAsBuyer);
                redirectAttributes.addFlashAttribute("message", "Giao dịch (EVOwner) thành công!");
                return "redirect:/marketplace";
            }

            throw new RuntimeException("Không tìm thấy thông tin người mua.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi: " + e.getMessage());
            return "redirect:/marketplace";
        }
    }

    // === tải PDF certificate (khi giao dịch COMPLETED) ===
    @GetMapping("/transactions/{id}/certificate")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable Long id, Principal principal) {
        var tx = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        // Bắt buộc đăng nhập đã được SecurityConfig kiểm soát; 
        // Ở đây kiểm tra thêm: nếu đọc được buyer/seller thì chỉ cho chính họ tải
        if (principal != null) {
            String username = principal.getName();
            String buyerU = partyUsername(getValue(tx, "getBuyer"));
            String sellerU = partyUsername(getValue(tx, "getSeller"));
            if (buyerU != null || sellerU != null) {
                boolean isParty = username.equals(buyerU) || username.equals(sellerU);
                if (!isParty) return ResponseEntity.status(403).build();
            }
        }

        // must be COMPLETED
        Object status = getValue(tx, "getStatus");
        if (status != null && !"COMPLETED".equalsIgnoreCase(status.toString())) {
            return ResponseEntity.badRequest()
                    .body(("Transaction " + id + " not completed").getBytes());
        }

        byte[] pdf = certificateService.generateCertificate(tx);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate_tx_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // --- reflection helpers ---

    private Object getValue(Object target, String method) {
        if (target == null) return null;
        try {
            Method m = target.getClass().getMethod(method);
            m.setAccessible(true);
            return m.invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String partyUsername(Object party) {
        if (party == null) return null;
        Object u = getValue(party, "getUsername");
        if (u != null) return String.valueOf(u);
        Object email = getValue(party, "getEmail");
        if (email != null) return String.valueOf(email);
        return null;
    }
}
