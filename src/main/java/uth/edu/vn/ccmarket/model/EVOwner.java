package uth.edu.vn.ccmarket.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EVOwner {
    private String ownerId;
    private String name;
    private String email;
    private Wallet wallet;
    private List<CarbonCredit> credits;

    public EVOwner(String name, String email) {
        this.ownerId = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.wallet = new Wallet(this.ownerId);
        this.credits = new ArrayList<>();
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public List<CarbonCredit> getCredits() {
        return credits;
    }

    public void addCredit(CarbonCredit c) {
        credits.add(c);
        wallet.depositCredits(c.getQuantity());
    }

    @Override
    public String toString() {
        return "EVOwner{" + "ownerId='" + ownerId + '\'' + ", name='" + name + '\'' + ", email='" + email + '\'' + '}';
    }
}
