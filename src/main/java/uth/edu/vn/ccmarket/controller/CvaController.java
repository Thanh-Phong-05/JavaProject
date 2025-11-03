package uth.edu.vn.ccmarket.controller;

import uth.edu.vn.ccmarket.model.CarbonCredit;
import uth.edu.vn.ccmarket.repository.CarbonCreditRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cva")
public class CvaController {

    private final CarbonCreditRepository creditRepo;

    public CvaController(CarbonCreditRepository creditRepo) {
        this.creditRepo = creditRepo;
    }

    @GetMapping("/dashboard")
    public String showCvaDashboard(Model model) {

        // tín chỉ chưa XM
        List<CarbonCredit> unverifiedCredits = creditRepo.findByVerified(false);

        // xuất ra file HTML
        model.addAttribute("credits", unverifiedCredits);

        return "cva-dashboard";
    }

    @PostMapping("/approve/{id}")
    public String approveCredit(
            @PathVariable("id") Long creditId,
            RedirectAttributes redirectAttributes) {

        try {

            CarbonCredit credit = creditRepo.findById(creditId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tín chỉ."));

            // duyêt tín chỉ
            credit.setVerified(true);

            creditRepo.save(credit);

            redirectAttributes.addFlashAttribute("message", "Duyệt tín chỉ #" + creditId + " thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi: " + e.getMessage());
        }

        return "redirect:/cva/dashboard";
    }
}