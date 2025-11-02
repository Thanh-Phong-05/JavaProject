package uth.edu.vn.ccmarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uth.edu.vn.ccmarket.service.MarketplaceService;

@Controller
public class MarketplaceController {
    private final MarketplaceService marketplace;

    public MarketplaceController(MarketplaceService marketplace) {
        this.marketplace = marketplace;
    }

    @GetMapping({ "/", "/marketplace" })
    public String marketplace(Model m) {
        m.addAttribute("listings", marketplace.findActiveListings());
        return "marketplace";
    }
}
