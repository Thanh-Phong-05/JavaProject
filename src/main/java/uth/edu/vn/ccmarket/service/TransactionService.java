package uth.edu.vn.ccmarket.service;

import uth.edu.vn.ccmarket.model.CCBuyer;
import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.model.Listing;
import uth.edu.vn.ccmarket.model.Transaction;
import uth.edu.vn.ccmarket.repository.CCBuyerRepository;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import uth.edu.vn.ccmarket.repository.ListingRepository;
import uth.edu.vn.ccmarket.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final ListingRepository listingRepo;
    private final EVOwnerRepository ownerRepo;
    private final CCBuyerRepository buyerRepo;
    private final TransactionRepository transactionRepo;

    public TransactionService(ListingRepository listingRepo,
            EVOwnerRepository ownerRepo,
            CCBuyerRepository buyerRepo,
            TransactionRepository transactionRepo) {
        this.listingRepo = listingRepo;
        this.ownerRepo = ownerRepo;
        this.buyerRepo = buyerRepo;
        this.transactionRepo = transactionRepo;
    }

    // xủ lý EVOwner mua hàng
    @Transactional
    public void executeBuyTransactionAsOwner(Long listingId, double quantity, EVOwner buyer) {

        Listing listing = listingRepo.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Rao bán không tồn tại."));

        EVOwner seller = ownerRepo.findById(listing.getSellerOwnerId())
                .orElseThrow(() -> new RuntimeException("Người bán không tồn tại."));

        validateTransaction(listing, quantity, buyer.getId(), buyer.getWallet().getCashBalance());

        double totalPrice = listing.getPricePerCredit() * quantity;

        buyer.getWallet().withdrawCash(totalPrice);
        buyer.getWallet().depositCredits(quantity);

        seller.getWallet().depositCash(totalPrice);

        updateListing(listing, quantity);

        createReceipt(listingId, buyer.getId(), seller.getId(), quantity, totalPrice);

        ownerRepo.save(buyer);
        ownerRepo.save(seller);
        listingRepo.save(listing);
    }

    // xủ lý CCBuyer mua hàng
    @Transactional
    public void executeBuyTransactionAsBuyer(Long listingId, double quantity, CCBuyer buyer) {

        Listing listing = listingRepo.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Rao bán không tồn tại."));

        EVOwner seller = ownerRepo.findById(listing.getSellerOwnerId())
                .orElseThrow(() -> new RuntimeException("Người bán không tồn tại."));

        validateTransaction(listing, quantity, buyer.getId(), buyer.getWallet().getCashBalance());

        double totalPrice = listing.getPricePerCredit() * quantity;

        buyer.getWallet().withdrawCash(totalPrice);
        buyer.getWallet().depositCredits(quantity);
        seller.getWallet().depositCash(totalPrice);

        updateListing(listing, quantity);
        createReceipt(listingId, buyer.getId(), seller.getId(), quantity, totalPrice);

        buyerRepo.save(buyer);
        ownerRepo.save(seller);
        listingRepo.save(listing);
    }

    private void validateTransaction(Listing listing, double quantity, Long buyerId, double buyerCashBalance) {
        if (!listing.isActive()) {
            throw new RuntimeException("Rao bán này đã kết thúc.");
        }
        if (listing.getQuantity() < quantity) {
            throw new RuntimeException("Số lượng không đủ, chỉ còn " + listing.getQuantity());
        }
        if (buyerId.equals(listing.getSellerOwnerId())) {
            throw new RuntimeException("Bạn không thể tự mua tín chỉ của chính mình.");
        }
        double totalPrice = listing.getPricePerCredit() * quantity;
        if (buyerCashBalance < totalPrice) {
            throw new RuntimeException("Bạn không đủ tiền (Cần " + totalPrice + " VND).");
        }
    }

    // Cập nhật listing sau khi giao dịch
    private void updateListing(Listing listing, double quantity) {
        listing.setQuantity(listing.getQuantity() - quantity);
        if (listing.getQuantity() == 0) {
            listing.setActive(false);
        }
    }

    // bien lai giao dich
    private void createReceipt(Long listingId, Long buyerId, Long sellerId, double quantity, double totalPrice) {
        Transaction receipt = new Transaction();
        receipt.setBuyerId(buyerId);
        receipt.setSellerId(sellerId);
        receipt.setListingId(listingId);
        receipt.setQuantity(quantity);
        receipt.setTotalPrice(totalPrice);
        receipt.setStatus("COMPLETED");

        transactionRepo.save(receipt);
    }
}