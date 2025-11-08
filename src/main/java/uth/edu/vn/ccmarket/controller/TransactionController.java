package uth.edu.vn.ccmarket.controller;

import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import uth.edu.vn.ccmarket.service.TransactionService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

@Controller
public class TransactionController {

    private final TransactionService transactionService;
    private final EVOwnerRepository ownerRepo;

    public TransactionController(TransactionService transactionService, EVOwnerRepository ownerRepo) {
        this.transactionService = transactionService;
        this.ownerRepo = ownerRepo;
    }

    @PostMapping("/buy")
    public String executeBuyTransaction(
            @RequestParam("listingId") Long listingId, // (3) Lấy từ form
            @RequestParam("quantity") double quantity, // (3) Lấy từ form
            Principal principal, // (4) Lấy người mua
            RedirectAttributes redirectAttributes) {

        // đăng nhập để mua
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            // Lấy thông tin chủ xe
            EVOwner buyer = ownerRepo.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người mua"));

            // Thực hiện giao dịch mua
            transactionService.executeBuyTransaction(listingId, quantity, buyer);

            redirectAttributes.addFlashAttribute("message", "Giao dịch thành công!");
            return "redirect:/marketplace";

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("message", "Lỗi: " + e.getMessage());
            return "redirect:/marketplace";
        }
    }
}