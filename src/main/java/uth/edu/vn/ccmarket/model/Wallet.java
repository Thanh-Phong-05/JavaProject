package uth.edu.vn.ccmarket.model;

public class Wallet {
    private String walletId;
    private double creditBalance; // number of carbon credits
    private double cashBalance; // currency, e.g., VND or USD

    public Wallet(String ownerId) {
        this.walletId = "WALLET-" + ownerId;
        this.creditBalance = 0.0;
        this.cashBalance = 0.0;
    }

    public String getWalletId() {
        return walletId;
    }

    public double getCreditBalance() {
        return creditBalance;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public void depositCredits(double qty) {
        creditBalance += qty;
    }

    public boolean withdrawCredits(double qty) {
        if (qty <= creditBalance) {
            creditBalance -= qty;
            return true;
        }
        return false;
    }

    public void depositCash(double amount) {
        cashBalance += amount;
    }

    public boolean withdrawCash(double amount) {
        if (amount <= cashBalance) {
            cashBalance -= amount;
            return true;
        }
        return false;
    }
}
