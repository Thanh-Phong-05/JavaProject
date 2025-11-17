package uth.edu.vn.ccmarket.config;

import uth.edu.vn.ccmarket.model.Admin;
import uth.edu.vn.ccmarket.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

// tạo tk ad tự động
@Component
public class DataSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataSeeder(AdminRepository adminRepository, BCryptPasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setUsername("admin");// tk
            admin.setPassword(passwordEncoder.encode("123")); // páss
            admin.setName("Super Administrator");
            admin.setAuthority("FULL_ACCESS");

            adminRepository.save(admin);
            System.out.println("______ ADMIN ACCOUNT CREATED: admin / 123 ______");
        }
    }
}