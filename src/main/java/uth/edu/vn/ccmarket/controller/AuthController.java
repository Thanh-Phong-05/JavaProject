package uth.edu.vn.ccmarket.controller;

import uth.edu.vn.ccmarket.model.CCBuyer;
import uth.edu.vn.ccmarket.repository.CCBuyerRepository;

import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.model.Wallet;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final EVOwnerRepository ownerRepo;
    private final CCBuyerRepository buyerRepo;
    private final BCryptPasswordEncoder encoder;

    public AuthController(EVOwnerRepository ownerRepo,
            CCBuyerRepository buyerRepo,
            BCryptPasswordEncoder encoder) {
        this.ownerRepo = ownerRepo;
        this.buyerRepo = buyerRepo;
        this.encoder = encoder;
    }

    @GetMapping("/register")
    public String registerForm(Model m) {
        m.addAttribute("user", new EVOwner());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute EVOwner user) {
        user.setPassword(encoder.encode(user.getPassword()));

        Wallet newWallet = new Wallet();
        newWallet.setOwner(user);
        user.setWallet(newWallet);

        ownerRepo.save(user);
        return "redirect:/login";
    }

    @GetMapping("/register-buyer")
    public String registerBuyerForm(Model m) {
        m.addAttribute("buyer", new CCBuyer());
        return "register-buyer"; // Trả về file HTML
    }

    @PostMapping("/register-buyer")
    public String doRegisterBuyer(@ModelAttribute CCBuyer buyer) {
        buyer.setPassword(encoder.encode(buyer.getPassword()));

        Wallet newWallet = new Wallet();
        newWallet.setBuyer(buyer);
        buyer.setWallet(newWallet);

        buyerRepo.save(buyer);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}