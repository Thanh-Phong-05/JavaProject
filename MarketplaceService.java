package uth.edu.vn.ccmarket.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uth.edu.vn.ccmarket.model.CarbonCredit;
import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.model.Listing;
import uth.edu.vn.ccmarket.model.Transaction;
import uth.edu.vn.ccmarket.repository.CarbonCreditRepository;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import uth.edu.vn.ccmarket.repository.ListingRepository;
import uth.edu.vn.ccmarket.repository.TransactionRepository;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class MarketplaceService {
    private final ListingRepository listingRepo;
    private final CarbonCreditRepository creditRepo;
    private final EVOwnerRepository ownerRepo;
    private final TransactionRepository txRepo;

    public MarketplaceService(ListingRepository listingRepo,
                              CarbonCreditRepository creditRepo,
                              EVOwnerRepository ownerRepo,
                              TransactionRepository txRepo) {
        this.listingRepo = listingRepo;
        this.creditRepo = creditRepo;
        this.ownerRepo = ownerRepo;
        this.txRepo = txRepo;
    }

    /**
     * Tạo listing dạng fixed-price:
     * - Yêu cầu tín chỉ đã verified
     * - Khóa (rút) số tín chỉ khỏi ví người bán (seller.withdrawCredits)
     * - Lưu listing đang active
     */
    @Transactional
    public Listing createListing(CarbonCredit cc, EVOwner seller, double qty, double pricePerCredit) {
        Objects.requireNonNull(cc, "CarbonCredit is null");
        Objects.requireNonNull(seller, "Seller is null");

        if (!cc.isVerified()) throw new IllegalStateException("Credit not verified");
        if (qty <= 0) throw new IllegalArgumentException("Quantity must be > 0");
        if (pricePerCredit <= 0) throw new IllegalArgumentException("Price must be > 0");
        if (qty > cc.getQuantity()) throw new IllegalArgumentException("Not enough credit available on this certificate");

        // Khóa tín chỉ trong ví người bán (đưa vào trạng thái “đem đi bán”)
        if (!seller.withdrawCredits(qty)) throw new IllegalStateException("Insufficient credits in seller wallet");

        Listing l = new Listing(cc.getId(), seller.getId(), qty, pricePerCredit);
        // nếu Listing có cờ active mặc định true thì ok; nếu không hãy set:
        l.setActive(true);
        return listingRepo.save(l);
    }

    /**
     * Người mua mua listing fixed:
     * - Không cho tự mua (buyer != seller)
     * - Kiểm tra còn active
     * - Trừ tiền buyer, cộng tiền seller
     * - Cộng credits cho buyer (vì credits đã bị khóa khỏi seller khi tạo listing)
     * - Đóng listing và ghi Transaction COMPLETED
     */
    @Transactional
    public Transaction buyListing(Long listingId, Long buyerId) {
        Listing l = listingRepo.findById(listingId).orElseThrow();
        if (!Boolean.TRUE.equals(l.isActive())) throw new IllegalStateException("Listing is not active");

        EVOwner buyer = ownerRepo.findById(buyerId).orElseThrow();
        EVOwner seller = ownerRepo.findById(l.getSellerOwnerId()).orElseThrow();

        if (Objects.equals(seller.getId(), buyer.getId()))
            throw new IllegalStateException("Cannot buy your own listing");

        double qty = l.getQuantity();
        double total = qty * l.getPricePerCredit();

        if (buyer.getCashBalance() < total) throw new IllegalStateException("Buyer not enough cash");

        // Thanh toán đơn giản (demo)
        buyer.depositCash(-total);
        seller.depositCash(total);

        // Chuyển credits cho buyer (vì seller đã withdraw khi tạo listing)
        buyer.depositCredits(qty);

        // Đóng listing
        l.setActive(false);
        listingRepo.save(l);

        // Ghi giao dịch
        Transaction tx = new Transaction();
        tx.setBuyerId(buyer.getId());
        tx.setSellerId(seller.getId());
        tx.setListingId(l.getId());
        tx.setQuantity(qty);
        tx.setTotalPrice(total);
        tx.setStatus("COMPLETED");
        tx.setCreatedAt(Instant.now()); // nếu model có createdAt kiểu Instant
        return txRepo.save(tx);
    }

    /**
     * Hủy listing (seller):
     * - Chỉ seller của listing được hủy
     * - Nếu listing đang active, mở khóa credits: trả credits lại ví seller
     * - Đánh dấu listing inactive
     */
    @Transactional
    public void cancelListing(Long listingId, Long sellerOwnerId) {
        Listing l = listingRepo.findById(listingId).orElseThrow();
        if (!Objects.equals(l.getSellerOwnerId(), sellerOwnerId))
            throw new IllegalStateException("Not seller of this listing");

        if (Boolean.TRUE.equals(l.isActive())) {
            // Mở khóa tín chỉ về ví seller
            EVOwner seller = ownerRepo.findById(sellerOwnerId).orElseThrow();
            seller.depositCredits(l.getQuantity());
            l.setActive(false);
            listingRepo.save(l);
            ownerRepo.save(seller);
        } else {
            // Đã inactive thì coi như idempotent
            l.setActive(false);
            listingRepo.save(l);
        }
    }

    /** Danh sách listing đang active */
    public List<Listing> findActiveListings() {
        return listingRepo.findByActiveTrue();
    }

    /**
     * Search/filter listing đang active theo min/max price, min/max qty, region (theo seller)
     * Giữ mức service đơn giản để dùng lại ở MVC.
     */
    public List<Listing> searchActive(Double minPrice, Double maxPrice,
                                      Double minQty,   Double maxQty,
                                      String regionOrNull) {
        Stream<Listing> stream = listingRepo.findByActiveTrue().stream();
        if (minPrice != null) stream = stream.filter(l -> l.getPricePerCredit() >= minPrice);
        if (maxPrice != null) stream = stream.filter(l -> l.getPricePerCredit() <= maxPrice);
        if (minQty   != null) stream = stream.filter(l -> l.getQuantity() >= minQty);
        if (maxQty   != null) stream = stream.filter(l -> l.getQuantity() <= maxQty);

        if (regionOrNull != null && !regionOrNull.isBlank()) {
            final String region = regionOrNull.trim();
            stream = stream.filter(l -> {
                EVOwner s = ownerRepo.findById(l.getSellerOwnerId()).orElse(null);
                return s != null && region.equalsIgnoreCase(s.getRegion());
            });
        }
        return stream.toList();
    }
}
