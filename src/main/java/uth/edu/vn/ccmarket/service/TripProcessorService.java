package uth.edu.vn.ccmarket.service;

import uth.edu.vn.ccmarket.model.Trip;
import uth.edu.vn.ccmarket.model.CarbonCredit;
import uth.edu.vn.ccmarket.model.EVOwner;

import java.util.List;

/**
 * Tính CO2 giảm phát thải từ các trip.
 * Giả sử heuristics:
 * - ICE average emission = 0.25 kg CO2/km
 * - EV grid emission = 0.05 kg CO2/km (nếu muốn chính xác cần LCA)
 * => giảm = 0.20 kg CO2/km = 0.0002 tonne/km
 * 1 carbon credit = 1 tonne CO2e
 */
public class TripProcessorService {

    private static final double ICE_EMISSION_KG_PER_KM = 0.25;
    private static final double EV_EMISSION_KG_PER_KM = 0.05;

    public CarbonCredit createCreditFromTrips(EVOwner owner, List<Trip> trips) {
        double totalReductionKg = trips.stream().mapToDouble(Trip::getDistanceKm).sum()
                * (ICE_EMISSION_KG_PER_KM - EV_EMISSION_KG_PER_KM);
        double tonnes = totalReductionKg / 1000.0;
        if (tonnes <= 0)
            throw new IllegalArgumentException("No reduction => no credit");
        CarbonCredit credit = new CarbonCredit(owner.getOwnerId(), tonnes);
        // Note: not verified yet — needs CVA.
        owner.addCredit(credit);
        return credit;
    }
}
