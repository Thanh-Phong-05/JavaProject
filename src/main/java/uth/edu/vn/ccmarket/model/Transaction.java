package uth.edu.vn.ccmarket.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long buyerId;
    private Long sellerId;
    private Long listingId;
    private double quantity;
    private double totalPrice;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String status;

    // getters/setters
    public Long getId() {
        return id;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long v) {
        buyerId = v;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long v) {
        sellerId = v;
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long v) {
        listingId = v;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double q) {
        quantity = q;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double p) {
        totalPrice = p;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String s) {
        status = s;
    }
}
