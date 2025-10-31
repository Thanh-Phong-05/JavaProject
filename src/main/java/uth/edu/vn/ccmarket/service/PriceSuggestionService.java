package uth.edu.vn.ccmarket.service;

import uth.edu.vn.ccmarket.model.Transaction;

import java.util.List;

/**
 * Heuristic price suggestion ("AI-like"):
 * - If there are past transactions, take average price per credit and add small
 * factor.
 * - Otherwise default to a baseline.
 */
public class PriceSuggestionService {

    private static final double BASELINE_PRICE = 10.0; // currency unit per credit

    public double suggestPricePerCredit(List<Transaction> pastTx) {
        if (pastTx == null || pastTx.isEmpty())
            return BASELINE_PRICE;
        double totalPrice = 0;
        double totalQty = 0;
        for (Transaction t : pastTx) {
            totalPrice += t.getTotalPrice();
            totalQty += t.getQuantity();
        }
        if (totalQty == 0)
            return BASELINE_PRICE;
        double avg = totalPrice / totalQty;
        // simple heuristic: recommend avg * (1 + 0.05) if market trending up
        return avg * 1.05;
    }
}
