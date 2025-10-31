package uth.edu.vn.ccmarket.model;

import java.time.LocalDate;
import java.util.UUID;

public class Listing {
    public enum Type {
        FIXED_PRICE, AUCTION
    }

    private String listingId;
    private String creditId;
    private String sellerOwnerId;
    private double quantity;
    private Type type;
    private double pricePerCredit; // for FIXED_PRICE; for auction it's starting price
    private LocalDate createdAt;
    private boolean active;

    public Listing(String creditId, String sellerOwnerId, double quantity, Type type, double pricePerCredit) {
        this.listingId = "LST-" + UUID.randomUUID().toString().substring(0, 8);
        this.creditId = creditId;
        this.sellerOwnerId = sellerOwnerId;
        this.quantity = quantity;
        this.type = type;
        this.pricePerCredit = pricePerCredit;
        this.createdAt = LocalDate.now();
        this.active = true;
    }

    public String getListingId() {
        return listingId;
    }

    public String getCreditId() {
        return creditId;
    }

    public String getSellerOwnerId() {
        return sellerOwnerId;
    }

    public double getQuantity() {
        return quantity;
    }

    public Type getType() {
        return type;
    }

    public double getPricePerCredit() {
        return pricePerCredit;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Listing{" + listingId + ", creditId=" + creditId + ", seller=" + sellerOwnerId + ", qty=" + quantity
                + ", type=" + type + ", price=" + pricePerCredit + "}";
    }
}
