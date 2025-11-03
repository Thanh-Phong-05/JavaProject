package uth.edu.vn.ccmarket.controller;

import uth.edu.vn.ccmarket.model.CarbonCredit;
import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.model.Trip;
import uth.edu.vn.ccmarket.repository.CarbonCreditRepository;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import uth.edu.vn.ccmarket.repository.TripRepository;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final EVOwnerRepository ownerRepo;
    private final TripRepository tripRepo;
    private final CarbonCreditRepository creditRepo;

    public DashboardController(EVOwnerRepository ownerRepo,
            TripRepository tripRepo,
            CarbonCreditRepository creditRepo) {
        this.ownerRepo = ownerRepo;
        this.tripRepo = tripRepo;
        this.creditRepo = creditRepo;
    }

    // Menu
    @GetMapping("/dashboard")
    public String showDashboard(Model model,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        EVOwner owner = ownerRepo.findByUsername(username);

        if (owner == null) {
            return "redirect:/";
        }

        List<Trip> trips = tripRepo.findByOwnerId(owner.getId());
        List<CarbonCredit> credits = creditRepo.findByOwnerId(owner.getId());

        double totalDistance = trips.stream()
                .mapToDouble(Trip::getDistanceKm)
                .sum();
        double totalCO2Saved = totalDistance * (0.25 - 0.05);

        model.addAttribute("owner", owner);
        model.addAttribute("trips", trips);
        model.addAttribute("credits", credits);
        model.addAttribute("totalDistance", totalDistance);
        model.addAttribute("totalCO2Saved", totalCO2Saved);

        return "dashboard";
    }
}