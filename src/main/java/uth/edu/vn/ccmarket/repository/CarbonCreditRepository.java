package uth.edu.vn.ccmarket.repository;

import uth.edu.vn.ccmarket.model.CarbonCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CarbonCreditRepository extends JpaRepository<CarbonCredit, Long> {
    List<CarbonCredit> findByOwnerId(Long ownerId);
}
