package uth.edu.vn.ccmarket.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uth.edu.vn.ccmarket.model.*;
import uth.edu.vn.ccmarket.repository.*;

import java.util.List;

@Service
public class MarketplaceService {
    private final ListingRepository listingRepo;
    private final CarbonCreditRepository creditRepo;
    private final EVOwnerRepository ownerRepo;
    private final TransactionRepository txRepo;

    public MarketplaceService(ListingRepository listingRepo, CarbonCreditRepository creditRepo,
            EVOwnerRepository ownerRepo, TransactionRepository txRepo) {
        this.listingRepo = listingRepo;
        this.creditRepo = creditRepo;
        this.ownerRepo = ownerRepo;
        this.txRepo = txRepo;
    }

    public Listing createListing(CarbonCredit cc, EVOwner seller, double qty, double pricePerCredit) {
        if (!cc.isVerified())
            throw new IllegalStateException("Credit not verified");
        if (qty > cc.getQuantity())
            throw new IllegalArgumentException("Not enough credit");
        if (!seller.withdrawCredits(qty))
            throw new IllegalStateException("Insufficient credits");
        Listing l = new Listing(cc.getId(), seller.getId(), qty, pricePerCredit);
        return listingRepo.save(l);
    }

    @Transactional
    public Transaction buyListing(Long listingId, Long buyerId) {
        Listing l = listingRepo.findById(listingId).orElseThrow();
        if (!l.isActive())
            throw new IllegalStateException("inactive");
        EVOwner buyer = ownerRepo.findById(buyerId).orElseThrow();
        EVOwner seller = ownerRepo.findById(l.getSellerOwnerId()).orElseThrow();
        double total = l.getQuantity() * l.getPricePerCredit();
        if (buyer.getCashBalance() < total)
            throw new IllegalStateException("buyer not enough cash");
        buyer.depositCash(-total);
        seller.depositCash(total);
        l.setActive(false);
        listingRepo.save(l);
        Transaction tx = new Transaction();
        tx.setBuyerId(buyerId);
        tx.setSellerId(seller.getId());
        tx.setListingId(l.getId());
        tx.setQuantity(l.getQuantity());
        tx.setTotalPrice(total);
        tx.setStatus("COMPLETED");
        return txRepo.save(tx);
    }

    public List<Listing> findActiveListings() {
        return listingRepo.findByActiveTrue();
    }
}
