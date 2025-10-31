package uth.edu.vn.ccmarket.model;

import java.time.LocalDate;
import java.util.UUID;

public class CarbonCredit {
    private String creditId;
    private String ownerId;
    private double quantity; // in tonnes CO2e (1 credit = 1 tonne)
    private LocalDate issuedDate;
    private boolean verified;

    public CarbonCredit(String ownerId, double quantity) {
        this.creditId = "CC-" + UUID.randomUUID().toString().substring(0, 8);
        this.ownerId = ownerId;
        this.quantity = quantity;
        this.issuedDate = LocalDate.now();
        this.verified = false;
    }

    public String getCreditId() {
        return creditId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public double getQuantity() {
        return quantity;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Override
    public String toString() {
        return "CarbonCredit{" + "creditId='" + creditId + '\'' + ", ownerId='" + ownerId + '\'' + ", quantity="
                + quantity + ", verified=" + verified + '}';
    }
}
