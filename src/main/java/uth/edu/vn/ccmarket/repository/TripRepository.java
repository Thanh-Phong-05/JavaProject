package uth.edu.vn.ccmarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uth.edu.vn.ccmarket.model.Trip;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByOwnerId(Long ownerId);
}
