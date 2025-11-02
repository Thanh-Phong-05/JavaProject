package uth.edu.vn.ccmarket.service;

import org.springframework.stereotype.Service;
import uth.edu.vn.ccmarket.model.CarbonCredit;

@Service
public class VerificationService {
    public boolean verify(CarbonCredit cc) {
        if (cc.getQuantity() > 0) {
            cc.setVerified(true);
            return true;
        }
        return false;
    }
}
