package uth.edu.vn.ccmarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uth.edu.vn.ccmarket.model.EVOwner;

import java.util.Optional;

public interface EVOwnerRepository extends JpaRepository<EVOwner, Long> {
    Optional<EVOwner> findByUsername(String username);
}
