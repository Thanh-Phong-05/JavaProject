package uth.edu.vn.ccmarket.repository;

import uth.edu.vn.ccmarket.model.EVOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EVOwnerRepository extends JpaRepository<EVOwner, Long> {
    Optional<EVOwner> findByUsername(String username);
}
