package uth.edu.vn.ccmarket.model;

import jakarta.persistence.*;

@Entity
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long creditId;
    private Long sellerOwnerId;
    private double quantity;
    private double pricePerCredit;
    private boolean active = true;

    public Listing() {
    }

    public Listing(Long creditId, Long sellerOwnerId, double quantity, double pricePerCredit) {
        this.creditId = creditId;
        this.sellerOwnerId = sellerOwnerId;
        this.quantity = quantity;
        this.pricePerCredit = pricePerCredit;
    }

    // getters/setters...
    public Long getId() {
        return id;
    }

    public Long getSellerOwnerId() {
        return sellerOwnerId;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getPricePerCredit() {
        return pricePerCredit;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean a) {
        this.active = a;
    }
}
