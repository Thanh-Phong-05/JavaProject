package uth.edu.vn.ccmarket.controller;

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
        repo.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
