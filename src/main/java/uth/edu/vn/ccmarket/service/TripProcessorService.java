package uth.edu.vn.ccmarket.service;

import uth.edu.vn.ccmarket.model.CarbonCredit;
import uth.edu.vn.ccmarket.model.EVOwner;
import org.springframework.stereotype.Service;

@Service
public class TripProcessorService {
    private static final double ICE_KG_PER_KM = 0.25;
    private static final double EV_KG_PER_KM = 0.05;

    public CarbonCredit createCreditFromKm(EVOwner owner, double totalKm) {
        double totalReductionKg = totalKm * (ICE_KG_PER_KM - EV_KG_PER_KM);
        double tonnes = totalReductionKg / 1000.0;
        if (tonnes <= 0)
            throw new IllegalArgumentException("No reduction");
        CarbonCredit cc = new CarbonCredit(owner.getId(), tonnes);
        owner.depositCredits(tonnes);
        return cc;
    }
}
