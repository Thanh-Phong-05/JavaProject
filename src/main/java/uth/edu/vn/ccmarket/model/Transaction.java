package uth.edu.vn.ccmarket.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    public enum Status {
        PENDING, COMPLETED, CANCELLED
    }

    private String txId;
    private String buyerId;
    private String sellerId;
    private String listingId;
    private double quantity;
    private double totalPrice;
    private LocalDateTime createdAt;
    private Status status;

    public Transaction(String buyerId, String sellerId, String listingId, double quantity, double totalPrice) {
        this.txId = "TX-" + UUID.randomUUID().toString().substring(0, 8);
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.listingId = listingId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.createdAt = LocalDateTime.now();
        this.status = Status.PENDING;
    }

    public String getTxId() {
        return txId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getListingId() {
        return listingId;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Transaction{" + txId + ", buyer=" + buyerId + ", seller=" + sellerId + ", listing=" + listingId
                + ", qty=" + quantity + ", total=" + totalPrice + ", status=" + status + "}";
    }
}
