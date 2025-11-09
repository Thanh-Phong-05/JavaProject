package uth.edu.vn.ccmarket.controller;

import uth.edu.vn.ccmarket.model.Wallet;
import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final EVOwnerRepository repo;
    private final BCryptPasswordEncoder encoder;

    public AuthController(EVOwnerRepository repo, BCryptPasswordEncoder encoder) {
        this.repo = repo;
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
        // cho user mới một cái ví
        Wallet newWallet = new Wallet(); // ví mới
        newWallet.setOwner(user); // chủ ví
        user.setWallet(newWallet); // ví cho chủ xe
        repo.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
