package uth.edu.vn.ccmarket.controller;

import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.model.Listing;
import uth.edu.vn.ccmarket.model.Transaction;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import uth.edu.vn.ccmarket.repository.ListingRepository;
import uth.edu.vn.ccmarket.repository.TransactionRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // admin xem hết dât
    private final EVOwnerRepository ownerRepo;
    private final ListingRepository listingRepo;
    private final TransactionRepository transactionRepo;

    public AdminController(EVOwnerRepository ownerRepo,
            ListingRepository listingRepo,
            TransactionRepository transactionRepo) {
        this.ownerRepo = ownerRepo;
        this.listingRepo = listingRepo;
        this.transactionRepo = transactionRepo;
    }

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {

        // TẤT CẢ dữ liệu từ
        List<EVOwner> allOwners = ownerRepo.findAll();
        List<Listing> allListings = listingRepo.findAll();
        List<Transaction> allTransactions = transactionRepo.findAll();

        model.addAttribute("owners", allOwners);
        model.addAttribute("listings", allListings);
        model.addAttribute("transactions", allTransactions);

        return "admin-dashboard";
    }

    @GetMapping
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/{id}/deposit") // nạp tiền cho user để test
    public String depositToUser(@PathVariable Long id,
            @RequestParam("amount") double amount,
            RedirectAttributes redirectAttributes) {
        try {
            EVOwner owner = ownerRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy User ID: " + id));

            owner.getWallet().depositCash(amount);

            ownerRepo.save(owner);

            redirectAttributes.addFlashAttribute("message",
                    "Đã nạp thành công " + String.format("%,.0f", amount) + " VND cho user: " + owner.getUsername());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }

}