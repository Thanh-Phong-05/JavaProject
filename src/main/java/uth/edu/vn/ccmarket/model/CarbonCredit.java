package uth.edu.vn.ccmarket.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class CarbonCredit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ownerId;
    private double quantity;
    private boolean verified = false;
    private LocalDate issuedAt = LocalDate.now();

    public CarbonCredit() {
    }

    public CarbonCredit(Long ownerId, double quantity) {
        this.ownerId = ownerId;
        this.quantity = quantity;
    }

    // getters/setters
    public Long getId() {
        return id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public double getQuantity() {
        return quantity;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean v) {
        this.verified = v;
    }
}
