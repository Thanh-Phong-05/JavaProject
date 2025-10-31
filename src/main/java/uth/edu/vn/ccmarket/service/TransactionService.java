package uth.edu.vn.ccmarket.service;

import uth.edu.vn.ccmarket.model.CCBuyer;
import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.model.Transaction;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple mapping to deposit funds to seller: in real world we'd have user repo.
 * For demo we'll register EVOwner and CCBuyer here.
 */
public class TransactionService {

    private Map<String, EVOwner> owners = new HashMap<>();
    private Map<String, CCBuyer> buyers = new HashMap<>();

    public void registerOwner(EVOwner owner) {
        owners.put(owner.getOwnerId(), owner);
    }

    public void registerBuyer(CCBuyer buyer) {
        buyers.put(buyer.getBuyerId(), buyer);
    }

    public void processCompletedTransaction(Transaction tx) {
        // deposit cash to seller wallet
        EVOwner seller = owners.get(tx.getSellerId());
        if (seller != null) {
            seller.getWallet().depositCash(tx.getTotalPrice());
        } else {
            System.out.println("Seller not found for tx: " + tx.getTxId());
        }
        // For buyer we already withdrew cash
    }
}
