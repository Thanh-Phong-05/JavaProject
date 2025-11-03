package uth.edu.vn.ccmarket.service;

import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EVOwnerRepository evOwnerRepository;

    public CustomUserDetailsService(EVOwnerRepository evOwnerRepository) {
        this.evOwnerRepository = evOwnerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        EVOwner owner = evOwnerRepository.findByUsername(username);

        if (owner == null) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng: " + username);
        }
        return new User(
                owner.getUsername(),
                owner.getPassword(),
                new ArrayList<>());
    }
}