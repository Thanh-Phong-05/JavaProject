package uth.edu.vn.ccmarket.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uth.edu.vn.ccmarket.model.CarbonCredit;
import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.model.VerificationRequest;
import uth.edu.vn.ccmarket.repository.CarbonCreditRepository;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import uth.edu.vn.ccmarket.repository.VerificationRequestRepository;

@Service
public class VerificationService {

    private final VerificationRequestRepository requestRepo;
    private final CarbonCreditRepository creditRepo;
    private final EVOwnerRepository ownerRepo;

    public VerificationService(VerificationRequestRepository requestRepo,
                               CarbonCreditRepository creditRepo,
                               EVOwnerRepository ownerRepo) {
        this.requestRepo = requestRepo;
        this.creditRepo = creditRepo;
        this.ownerRepo = ownerRepo;
    }

    @Transactional
    public VerificationRequest createRequest(Long creditId, Long ownerId) {
        CarbonCredit credit = creditRepo.findById(creditId)
                .orElseThrow(() -> new IllegalArgumentException("Credit not found"));
        if (credit.isVerified()) {
            throw new IllegalStateException("Credit đã verified");
        }
        EVOwner owner = ownerRepo.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
        if (!owner.getId().equals(credit.getOwner().getId())) {
            throw new IllegalStateException("Bạn không sở hữu credit này");
        }

        // chặn trùng request PENDING/REJECTED->PENDING tuỳ chính sách; ở đây chặn nếu PENDING/APPROVED đã tồn tại
        VerificationRequest.Status[] active = new VerificationRequest.Status[]{
                VerificationRequest.Status.PENDING, VerificationRequest.Status.APPROVED
        };
        Optional<VerificationRequest> existing = requestRepo.findFirstByCreditIdAndStatusIn(creditId, active);
        if (existing.isPresent()) {
            throw new IllegalStateException("Đã có yêu cầu đang tồn tại cho credit này");
        }

        VerificationRequest vr = new VerificationRequest(creditId, ownerId);
        return requestRepo.save(vr);
    }

    public Page<VerificationRequest> listByStatus(VerificationRequest.Status status, int page, int size) {
        if (status == null) {
            return requestRepo.findAll(PageRequest.of(page, size));
        }
        return requestRepo.findByStatus(status, PageRequest.of(page, size));
    }

    @Transactional
    public VerificationRequest approve(Long id, String decidedBy, String note) {
        VerificationRequest vr = requestRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (vr.getStatus() != VerificationRequest.Status.PENDING) {
            throw new IllegalStateException("Trạng thái không hợp lệ để approve");
        }

        CarbonCredit credit = creditRepo.findById(vr.getCreditId())
                .orElseThrow(() -> new IllegalStateException("Credit not found"));

        credit.setVerified(true);
        creditRepo.save(credit);

        vr.setStatus(VerificationRequest.Status.APPROVED);
        vr.setDecidedAt(LocalDateTime.now());
        vr.setDecidedBy(decidedBy);
        vr.setNote(note);
        return requestRepo.save(vr);
    }

    @Transactional
    public VerificationRequest reject(Long id, String decidedBy, String note) {
        VerificationRequest vr = requestRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (vr.getStatus() != VerificationRequest.Status.PENDING) {
            throw new IllegalStateException("Trạng thái không hợp lệ để reject");
        }

        vr.setStatus(VerificationRequest.Status.REJECTED);
        vr.setDecidedAt(LocalDateTime.now());
        vr.setDecidedBy(decidedBy);
        vr.setNote(note);
        return requestRepo.save(vr);
    }
}
