package uth.edu.vn.ccmarket.service;

import uth.edu.vn.ccmarket.model.CVA;
import uth.edu.vn.ccmarket.model.CarbonCredit;

/**
 * CVA checks (very simple): if quantity > 0 and owner exists => verify
 */
public class VerificationService {

    public boolean verifyCredit(CVA cva, CarbonCredit credit) {
        // In real life, would check telemetry, raw data, signatures...
        if (credit.getQuantity() > 0) {
            credit.setVerified(true);
            System.out.println("CVA " + cva.getName() + " approved credit " + credit.getCreditId());
            return true;
        } else {
            System.out.println("CVA " + cva.getName() + " rejected credit " + credit.getCreditId());
            return false;
        }
    }
}
