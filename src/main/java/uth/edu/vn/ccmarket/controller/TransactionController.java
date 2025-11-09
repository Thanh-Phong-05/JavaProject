package uth.edu.vn.ccmarket.controller;

import uth.edu.vn.ccmarket.model.CCBuyer;
import uth.edu.vn.ccmarket.repository.CCBuyerRepository;

import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import uth.edu.vn.ccmarket.service.TransactionService;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TransactionController {

    private final TransactionService transactionService;
    private final EVOwnerRepository ownerRepo;
    private final CCBuyerRepository buyerRepo; // người mua

    public TransactionController(TransactionService transactionService,
            EVOwnerRepository ownerRepo,
            CCBuyerRepository buyerRepo) {
        this.transactionService = transactionService;
        this.ownerRepo = ownerRepo;
        this.buyerRepo = buyerRepo;
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

        // username ng đăng nhập
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
}