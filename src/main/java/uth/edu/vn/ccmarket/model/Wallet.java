package uth.edu.vn.ccmarket.model;

import jakarta.persistence.*;

@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double cashBalance = 0.0;
    private double creditBalance = 0.0;
    // ví cho chủ xe
    @OneToOne
    @JoinColumn(name = "owner_id", nullable = true)
    private EVOwner owner;
    // ví cho người mua
    @OneToOne
    @JoinColumn(name = "buyer_id", nullable = true)
    private CCBuyer buyer;

    // giao dịch tín chỉ
    public void depositCredits(double q) {
        if (q > 0)
            this.creditBalance += q;
    }

    public boolean withdrawCredits(double q) {
        if (q <= 0)
            return false;
        if (q <= this.creditBalance) {
            this.creditBalance -= q;
            return true;
        }
        return false;
    }

    public void depositCash(double v) {
        if (v > 0)
            this.cashBalance += v;
    }

    public boolean withdrawCash(double v) {
        if (v <= 0)
            return false;
        if (v <= this.cashBalance) {
            this.cashBalance -= v;
            return true;
        }
        return false;
    }

    public Wallet() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(double cashBalance) {
        this.cashBalance = cashBalance;
    }

    public double getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(double creditBalance) {
        this.creditBalance = creditBalance;
    }

    // Get/Set chủ xe
    public EVOwner getOwner() {
        return owner;
    }

    public void setOwner(EVOwner owner) {
        this.owner = owner;
    }

    // Get/Set người mua
    public CCBuyer getBuyer() {
        return buyer;
    }

    public void setBuyer(CCBuyer buyer) {
        this.buyer = buyer;
    }
}