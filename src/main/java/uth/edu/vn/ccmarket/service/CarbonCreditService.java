package uth.edu.vn.ccmarket.service;

import org.springframework.stereotype.Service;

@Service
public class CarbonCreditService {

    private static final double KG_CO2_PER_CREDIT = 1000.0; // 1 credit = 1000 kg CO₂

    public double convertCO2ToCredits(double totalCO2SavedKg) {
        return totalCO2SavedKg / KG_CO2_PER_CREDIT;
    }
}
