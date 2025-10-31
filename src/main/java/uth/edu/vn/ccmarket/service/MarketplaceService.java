package uth.edu.vn.ccmarket.service;

import uth.edu.vn.ccmarket.model.CarbonCredit;
import uth.edu.vn.ccmarket.model.CCBuyer;
import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.model.Listing;
import uth.edu.vn.ccmarket.model.Transaction;

import java.util.*;
import java.util.stream.Collectors;

public class MarketplaceService {

    // in-memory stores
    private Map<String, CarbonCredit> creditStore = new HashMap<>();
    private Map<String, Listing> listingStore = new HashMap<>();
    private Map<String, Transaction> txStore = new HashMap<>();
    private List<Transaction> completedTransactions = new ArrayList<>();

    private TransactionService transactionService;
    private PriceSuggestionService priceSuggestionService;

    public MarketplaceService(TransactionService transactionService, PriceSuggestionService priceSuggestionService) {
        this.transactionService = transactionService;
        this.priceSuggestionService = priceSuggestionService;
    }

    public void registerCredit(CarbonCredit cc) {
        creditStore.put(cc.getCreditId(), cc);
    }

    public Listing createListing(CarbonCredit cc, EVOwner seller, double qty, Listing.Type type,
            double pricePerCredit) {
        if (!cc.isVerified())
            throw new IllegalStateException("Credit not verified");
        if (qty > cc.getQuantity())
            throw new IllegalArgumentException("Not enough credit quantity");
        if (!seller.getWallet().withdrawCredits(qty))
            throw new IllegalStateException("Seller wallet insufficient credits");

        Listing l = new Listing(cc.getCreditId(), seller.getOwnerId(), qty, type, pricePerCredit);
        listingStore.put(l.getListingId(), l);
        System.out.println("Listing created: " + l);
        return l;
    }

    public List<Listing> searchListings(Double minQty, Double maxPrice) {
        return listingStore.values().stream().filter(Listing::isActive)
                .filter(l -> (minQty == null || l.getQuantity() >= minQty)
                        && (maxPrice == null || l.getPricePerCredit() <= maxPrice))
                .collect(Collectors.toList());
    }

    public Transaction buyFixedPrice(CCBuyer buyer, Listing listing) {
        if (!listing.isActive())
            throw new IllegalStateException("Listing not active");
        double total = listing.getQuantity() * listing.getPricePerCredit();
        if (!buyer.getWallet().withdrawCash(total))
            throw new IllegalStateException("Buyer has insufficient cash");
        Transaction tx = new Transaction(buyer.getBuyerId(), listing.getSellerOwnerId(), listing.getListingId(),
                listing.getQuantity(), total);
        tx.setStatus(Transaction.Status.COMPLETED);
        listing.setActive(false);
        // Transfer cash to seller wallet (in real system via payment gateway)
        // For demo: simulate seller deposit by exposing seller wallet externally
        // through TransactionService
        transactionService.processCompletedTransaction(tx);
        txStore.put(tx.getTxId(), tx);
        completedTransactions.add(tx);
        System.out.println("Transaction completed: " + tx);
        return tx;
    }

    public void registerTransaction(Transaction tx) {
        txStore.put(tx.getTxId(), tx);
    }

    public List<Transaction> getCompletedTransactions() {
        return completedTransactions;
    }

    public PriceSuggestionService getPriceSuggestionService() {
        return priceSuggestionService;
    }
}
