package uth.edu.vn.ccmarket.controller;

import uth.edu.vn.ccmarket.model.CarbonCredit;
import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.model.Trip;
import uth.edu.vn.ccmarket.repository.CarbonCreditRepository;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import uth.edu.vn.ccmarket.repository.TripRepository;
import uth.edu.vn.ccmarket.repository.TransactionRepository;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import uth.edu.vn.ccmarket.model.Transaction;

@Controller
public class DashboardController {

    private final EVOwnerRepository ownerRepo;
    private final TripRepository tripRepo;
    private final CarbonCreditRepository creditRepo;
    private final TransactionRepository transactionRepo;

    public DashboardController(EVOwnerRepository ownerRepo,
            TripRepository tripRepo,
            CarbonCreditRepository creditRepo, TransactionRepository transactionRepo) {
        this.ownerRepo = ownerRepo;
        this.tripRepo = tripRepo;
        this.creditRepo = creditRepo;
        this.transactionRepo = transactionRepo;
    }

    // Menu
    @GetMapping("/dashboard")
    public String showDashboard(Model model,
            java.security.Principal principal) {
        String username = principal.getName();
        EVOwner owner = ownerRepo.findByUsername(username).orElse(null);

        if (owner == null) {
            return "redirect:/";
        }

        List<Trip> trips = tripRepo.findByOwnerId(owner.getId());
        List<CarbonCredit> credits = creditRepo.findByOwnerId(owner.getId());
        List<Transaction> transactions = transactionRepo.findBySellerId(owner.getId());
        double totalDistance = trips.stream()
                .mapToDouble(Trip::getDistanceKm)
                .sum();
        double totalCO2Saved = totalDistance * (0.25 - 0.05);

        model.addAttribute("owner", owner);
        model.addAttribute("trips", trips);
        model.addAttribute("credits", credits);
        model.addAttribute("totalDistance", totalDistance);
        model.addAttribute("totalCO2Saved", totalCO2Saved);
        model.addAttribute("transactions", transactions);
        return "dashboard";
    }
}