package uth.edu.vn.ccmarket.service;

import uth.edu.vn.ccmarket.model.Admin;
import uth.edu.vn.ccmarket.model.CCBuyer;
import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.repository.CCBuyerRepository;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import uth.edu.vn.ccmarket.repository.AdminRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EVOwnerRepository evOwnerRepository;
    private final CCBuyerRepository buyerRepository; // check ng mua
    private final AdminRepository adminRepository;

    public CustomUserDetailsService(EVOwnerRepository evOwnerRepository, CCBuyerRepository buyerRepository,
            AdminRepository adminRepository) {
        this.evOwnerRepository = evOwnerRepository;
        this.buyerRepository = buyerRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // check chu xe
        Optional<EVOwner> ownerOpt = evOwnerRepository.findByUsername(username);

        if (ownerOpt.isPresent()) {
            EVOwner owner = ownerOpt.get();
            return new User(
                    owner.getUsername(),
                    owner.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_EV_OWNER")));
        }

        // check sang nguoi mua
        Optional<CCBuyer> buyerOpt = buyerRepository.findByUsername(username);

        if (buyerOpt.isPresent()) {
            CCBuyer buyer = buyerOpt.get();
            return new User(
                    buyer.getUsername(),
                    buyer.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_CC_BUYER")));
        }
        // check admin
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            return new User(adminOpt.get().getUsername(), adminOpt.get().getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        }

        throw new UsernameNotFoundException("Không tìm thấy người dùng (trong cả EVOwner và CCBuyer): " + username);
    }
}