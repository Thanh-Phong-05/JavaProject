package uth.edu.vn.ccmarket.service;

import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.model.Listing;
import uth.edu.vn.ccmarket.model.Transaction;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import uth.edu.vn.ccmarket.repository.ListingRepository;
import uth.edu.vn.ccmarket.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // (1) Import phép thuật

@Service
public class TransactionService {

    private final ListingRepository listingRepo;
    private final EVOwnerRepository ownerRepo;
    private final TransactionRepository transactionRepo;

    public TransactionService(ListingRepository listingRepo, EVOwnerRepository ownerRepo,
            TransactionRepository transactionRepo) {
        this.listingRepo = listingRepo;
        this.ownerRepo = ownerRepo;
        this.transactionRepo = transactionRepo;
    }

    @Transactional
    public void executeBuyTransaction(Long listingId, double quantity, EVOwner buyer) {

        // Lấy rao bántừ db
        Listing listing = listingRepo.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Rao bán không tồn tại."));

        // người mua (chủ xe)
        EVOwner seller = ownerRepo.findById(listing.getSellerOwnerId())
                .orElseThrow(() -> new RuntimeException("Người bán không tồn tại."));

        if (!listing.isActive()) {
            throw new RuntimeException("Rao bán này đã kết thúc.");
        }
        if (listing.getQuantity() < quantity) {
            throw new RuntimeException("Số lượng không đủ, chỉ còn " + listing.getQuantity());
        }

        if (buyer.getId().equals(seller.getId())) {
            throw new RuntimeException("Bạn không thể tự mua tín chỉ của chính mình.");
        }

        double totalPrice = listing.getPricePerCredit() * quantity;
        if (buyer.getWallet().getCashBalance() < totalPrice) {// lấy tiền từ ví
            throw new RuntimeException("Bạn không đủ tiền (Cần " + totalPrice + " VND).");
        }

        // gd trừ tiền người mua
        buyer.getWallet().withdrawCash(totalPrice);
        // cộng tín chỉ cho người mua
        buyer.getWallet().depositCredits(quantity);

        // cộng tiền cho người bán
        seller.getWallet().depositCash(totalPrice);

        // cập nhật lại rao bán (trừ số lượng)
        listing.setQuantity(listing.getQuantity() - quantity);
        if (listing.getQuantity() == 0) {
            listing.setActive(false);
        }

        // lịch sử giao dịch
        Transaction receipt = new Transaction();
        receipt.setBuyerId(buyer.getId());
        receipt.setSellerId(seller.getId());
        receipt.setListingId(listingId);
        receipt.setQuantity(quantity);
        receipt.setTotalPrice(totalPrice);
        receipt.setStatus("COMPLETED");

        ownerRepo.save(buyer);
        ownerRepo.save(seller);
        listingRepo.save(listing);
        transactionRepo.save(receipt);
    }
}