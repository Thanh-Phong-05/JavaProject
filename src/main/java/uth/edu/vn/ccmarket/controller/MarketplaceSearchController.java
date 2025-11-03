package uth.edu.vn.ccmarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import uth.edu.vn.ccmarket.service.MarketplaceService;

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
    public String search(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minQty,
            @RequestParam(required = false) Double maxQty,
            @RequestParam(required = false) String region,
            Model model) {

        // Dùng hàm có sẵn trong MarketplaceService (đã tối ưu)
        var filtered = marketplace.searchActive(minPrice, maxPrice, minQty, maxQty, region);

        model.addAttribute("listings", filtered);

        // Giữ lại các giá trị filter trong form
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("minQty", minQty);
        model.addAttribute("maxQty", maxQty);
        model.addAttribute("region", region);

        return "marketplace"; // view hiển thị listings
    }
}
