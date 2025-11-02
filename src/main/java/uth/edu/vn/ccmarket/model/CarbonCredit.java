package uth.edu.vn.ccmarket.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "carbon_credit")
public class CarbonCredit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) // nhiều tín chỉ - 1 chủ xe
    @JoinColumn(name = "owner_id", nullable = false)
    private EVOwner owner;// chủ sở hữu tín chỉ
    private double quantity; // số lượng tín chỉ (kg CO2)
    private LocalDate issuedAt;
    private boolean verified = false;// xác minh hay chưa

    public CarbonCredit() {
    }

    public CarbonCredit(EVOwner owner, double quantity, LocalDate issuedAt) {
        this.owner = owner;
        this.quantity = quantity;
        this.issuedAt = issuedAt;
    }

    public CarbonCredit(EVOwner owner, double quantity) {
        this.owner = owner;
        this.quantity = quantity;
        this.issuedAt = LocalDate.now();
        this.verified = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EVOwner getOwner() {
        return owner;
    }

    public void setOwner(EVOwner owner) {
        this.owner = owner;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public LocalDate getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDate issuedAt) {
        this.issuedAt = issuedAt;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
