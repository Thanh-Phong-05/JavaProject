package uth.edu.vn.ccmarket.repository;

import uth.edu.vn.ccmarket.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByActive(boolean active);
}
