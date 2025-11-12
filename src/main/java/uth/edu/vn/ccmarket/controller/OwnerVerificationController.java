package uth.edu.vn.ccmarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.model.VerificationRequest;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import uth.edu.vn.ccmarket.service.VerificationService;

import java.security.Principal;

@Controller
@RequestMapping("/owner")
public class OwnerVerificationController {

    private final VerificationService verificationService;
    private final EVOwnerRepository ownerRepo;

    public OwnerVerificationController(VerificationService verificationService,
                                       EVOwnerRepository ownerRepo) {
        this.verificationService = verificationService;
        this.ownerRepo = ownerRepo;
    }

    @PostMapping("/credits/{creditId}/request-issue")
    public String requestIssue(@PathVariable Long creditId,
                               @RequestParam(value = "note", required = false) String note,
                               Principal principal,
                               RedirectAttributes redirect) {
        if (principal == null) return "redirect:/login";

        String username = principal.getName();
        EVOwner owner = ownerRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        try {
            VerificationRequest vr = verificationService.createRequest(creditId, owner.getId());
            if (note != null && !note.isBlank()) {
                vr.setNote(note);
            }
            redirect.addFlashAttribute("message", "Đã gửi yêu cầu kiểm định #" + vr.getId());
        } catch (Exception e) {
            redirect.addFlashAttribute("message", "Lỗi: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }
}
