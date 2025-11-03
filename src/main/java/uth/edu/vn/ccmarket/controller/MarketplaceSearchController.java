package uth.edu.vn.ccmarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import uth.edu.vn.ccmarket.service.MarketplaceService;

import java.util.List;

@Controller
@RequestMapping("/marketplace")
public class MarketplaceSearchController {
    private final MarketplaceService marketplace;
    private final EVOwnerRepository ownerRepo;

    public MarketplaceSearchController(MarketplaceService marketplace, EVOwnerRepository ownerRepo) {
        this.marketplace = marketplace;
        this.ownerRepo = ownerRepo;
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) Double minPrice,
                         @RequestParam(required = false) Double maxPrice,
                         @RequestParam(required = false) Double minQty,
                         @RequestParam(required = false) Double maxQty,
                         @RequestParam(required = false) String region,
                         Model m) {
        var all = marketplace.findActiveListings();
        var stream = all.stream();
        if (minPrice != null) stream = stream.filter(l -> l.getPricePerCredit() >= minPrice);
        if (maxPrice != null) stream = stream.filter(l -> l.getPricePerCredit() <= maxPrice);
        if (minQty   != null) stream = stream.filter(l -> l.getQuantity() >= minQty);
        if (maxQty   != null) stream = stream.filter(l -> l.getQuantity() <= maxQty);
        if (region != null && !region.isBlank()) {
            stream = stream.filter(l -> {
                EVOwner s = ownerRepo.findById(l.getSellerOwnerId()).orElse(null);
                return s != null && region.equalsIgnoreCase(s.getRegion());
            });
        }
        m.addAttribute("listings", stream.toList());
        // thêm các giá trị filter để giữ form
        m.addAttribute("minPrice", minPrice); m.addAttribute("maxPrice", maxPrice);
        m.addAttribute("minQty", minQty);     m.addAttribute("maxQty", maxQty);
        m.addAttribute("region", region);
        return "marketplace"; // dùng lại view marketplace.html
    }
}
