package uth.edu.vn.ccmarket.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import uth.edu.vn.ccmarket.model.VerificationRequest;

public interface VerificationRequestRepository extends JpaRepository<VerificationRequest, Long> {

    Page<VerificationRequest> findByStatus(VerificationRequest.Status status, Pageable pageable);

    Optional<VerificationRequest> findFirstByCreditIdAndStatusIn(Long creditId,
                                                                 VerificationRequest.Status[] statuses);
}
