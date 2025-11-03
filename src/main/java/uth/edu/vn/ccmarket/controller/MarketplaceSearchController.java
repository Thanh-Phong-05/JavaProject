package uth.edu.vn.ccmarket.controller;

import uth.edu.vn.ccmarket.model.Listing;
import uth.edu.vn.ccmarket.repository.ListingRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * (1) Controller này xử lý việc HIỂN THỊ và TÌM KIẾM
 * trên trang marketplace công khai.
 */
@Controller
public class MarketplaceSearchController {

    private final ListingRepository listingRepo;

    public MarketplaceSearchController(ListingRepository listingRepo) {
        this.listingRepo = listingRepo;
    }

    @GetMapping("/marketplace")
    public String showMarketplace(Model model) {

        List<Listing> listings = listingRepo.findByActive(true);

        model.addAttribute("listings", listings);

        return "marketplace";
    }
}