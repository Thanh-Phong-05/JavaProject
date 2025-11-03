package uth.edu.vn.ccmarket.controller;

import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.model.Listing;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import uth.edu.vn.ccmarket.repository.ListingRepository;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/listings")
public class MarketplaceController {

    private final EVOwnerRepository ownerRepo;
    private final ListingRepository listingRepo;

    public MarketplaceController(EVOwnerRepository ownerRepo, ListingRepository listingRepo) {
        this.ownerRepo = ownerRepo;
        this.listingRepo = listingRepo;
    }

    /**
     * xử lý yêu cầu rao bán
     * URL đầy đủ: POST /listings/create
     */
    @PostMapping("/create")
    public String createListing(
            @RequestParam("quantity") double quantity,
            @RequestParam("price") double price,
            @AuthenticationPrincipal UserDetails userDetails, // chủ xe
            RedirectAttributes redirectAttributes) {

        EVOwner owner = ownerRepo.findByUsername(userDetails.getUsername()).orElse(null);
        if (owner == null) {
            return "redirect:/login"; // không thấy user
        }

        if (quantity <= 0 || price <= 0) {
            redirectAttributes.addFlashAttribute("message", "Lỗi: Số lượng và giá phải lớn hơn 0.");
            return "redirect:/dashboard";
        }

        if (owner.getCreditBalance() < quantity) {
            redirectAttributes.addFlashAttribute("message", "Lỗi: Bạn không đủ tín chỉ để bán.");
            return "redirect:/dashboard";
        }

        // Trừ tín chỉ khỏi ví
        boolean success = owner.withdrawCredits(quantity); //

        if (success) {
            Listing newListing = new Listing(null, owner.getId(), quantity, price);
            ownerRepo.save(owner);
            listingRepo.save(newListing);
            redirectAttributes.addFlashAttribute("message", "Niêm yết bán thành công!");
        } else {
            redirectAttributes.addFlashAttribute("message", "Lỗi: Rút tín chỉ thất bại.");
        }

        return "redirect:/dashboard";
    }
}