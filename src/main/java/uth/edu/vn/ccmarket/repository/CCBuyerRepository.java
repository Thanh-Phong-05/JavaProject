package uth.edu.vn.ccmarket.repository;

import uth.edu.vn.ccmarket.model.CCBuyer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CCBuyerRepository extends JpaRepository<CCBuyer, Long> {

    Optional<CCBuyer> findByUsername(String username);
}