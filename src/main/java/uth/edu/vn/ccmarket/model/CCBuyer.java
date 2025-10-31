package uth.edu.vn.ccmarket.model;

public class CCBuyer {
    private String buyerId;
    private String name;
    private Wallet wallet;

    public CCBuyer(String name) {
        this.buyerId = "BUYER-" + name + "-" + System.currentTimeMillis();
        this.name = name;
        this.wallet = new Wallet(buyerId);
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getName() {
        return name;
    }

    public Wallet getWallet() {
        return wallet;
    }

    @Override
    public String toString() {
        return "CCBuyer{" + buyerId + ", name=" + name + "}";
    }
}
