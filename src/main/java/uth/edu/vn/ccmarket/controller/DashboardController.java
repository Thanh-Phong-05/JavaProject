package uth.edu.vn.ccmarket.controller;

import uth.edu.vn.ccmarket.model.*;
import uth.edu.vn.ccmarket.repository.*;
import uth.edu.vn.ccmarket.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {
    private final EVOwnerRepository ownerRepo;
    private final CarbonCreditRepository creditRepo;
    private final TripProcessorService tripService;
    private final VerificationService verificationService;
    private final MarketplaceService marketplace;

    public DashboardController(EVOwnerRepository ownerRepo, CarbonCreditRepository creditRepo,
            TripProcessorService tripService, VerificationService verificationService,
            MarketplaceService marketplace) {
        this.ownerRepo = ownerRepo;
        this.creditRepo = creditRepo;
        this.tripService = tripService;
        this.verificationService = verificationService;
        this.marketplace = marketplace;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model m) {
        EVOwner me = ownerRepo.findByUsername(user.getUsername()).orElseThrow();
        m.addAttribute("me", me);
        m.addAttribute("credits", creditRepo.findByOwnerId(me.getId()));
        m.addAttribute("listings", marketplace.findActiveListings().stream()
                .filter(l -> l.getSellerOwnerId().equals(me.getId())).toList());
        return "dashboard";
    }

    @PostMapping("/uploadKm")
    public String uploadKm(@AuthenticationPrincipal User user, @RequestParam double km) {
        EVOwner me = ownerRepo.findByUsername(user.getUsername()).orElseThrow();
        CarbonCredit cc = tripService.createCreditFromKm(me, km);
        creditRepo.save(cc);
        ownerRepo.save(me);
        return "redirect:/dashboard";
    }

    @PostMapping("/verifyCredit")
    public String verifyCredit(@RequestParam Long creditId) {
        CarbonCredit cc = creditRepo.findById(creditId).orElseThrow();
        verificationService.verify(cc);
        creditRepo.save(cc);
        return "redirect:/dashboard";
    }

    @PostMapping("/createListing")
    public String createListing(@AuthenticationPrincipal User user, @RequestParam Long creditId,
            @RequestParam double qty, @RequestParam double price) {
        EVOwner me = ownerRepo.findByUsername(user.getUsername()).orElseThrow();
        CarbonCredit cc = creditRepo.findById(creditId).orElseThrow();
        marketplace.createListing(cc, me, qty, price);
        ownerRepo.save(me);
        return "redirect:/dashboard";
    }
}
