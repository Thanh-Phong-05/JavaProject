package uth.edu.vn.ccmarket.service;

import uth.edu.vn.ccmarket.model.*;
import uth.edu.vn.ccmarket.repository.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TripProcessorService {
    private static final double ICE_EMISSION_KG_PER_KM = 0.25; // xe xăng thải 0.25 kg CO2/km
    private static final double EV_EMISSION_KG_PER_KM = 0.05; // xe điện thải 0.05 kg CO2/km
    // 1 carbon credit = 1000 kg CO2

    private final TripRepository tripRepo;
    private final CarbonCreditRepository creditRepo; // lưu Trip và CarbonCredit

    public TripProcessorService(TripRepository tripRepo, CarbonCreditRepository creditRepo) {
        this.tripRepo = tripRepo;
        this.creditRepo = creditRepo;
    }

    public List<Trip> parseTripsCsv(InputStream csvInput, EVOwner owner) throws Exception {
        List<Trip> trips = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvInput))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(",");
                double distanceKm = Double.parseDouble(parts[0].trim());
                double energy = parts.length > 1 && !parts[1].trim().isEmpty() ? Double.parseDouble(parts[1].trim())
                        : 0.0;
                LocalDate date = parts.length > 2 && !parts[2].trim().isEmpty() ? LocalDate.parse(parts[2].trim())
                        : LocalDate.now();
                Trip t = new Trip(distanceKm, energy, date, owner);
                trips.add(t);
            }
        } catch (IOException e) {
            throw new Exception("Error reading CSV file", e);
        }
        return tripRepo.saveAll(trips);
    }

    public CarbonCredit createCreditFromTrips(EVOwner owner, List<Trip> trips) {
        double totalDistance = trips.stream().mapToDouble(Trip::getDistanceKm).sum();// tổng km đã đi
        double savedKg = totalDistance * (ICE_EMISSION_KG_PER_KM - EV_EMISSION_KG_PER_KM);// kg CO2 đã tiết kiệm
        double tonnes = savedKg / 1000.0;// đổi ra tấn CO2
        if (tonnes <= 0)
            throw new IllegalArgumentException("No credit generated");
        CarbonCredit cc = new CarbonCredit(owner, tonnes);// tạo tín chỉ cacbon
        cc.setVerified(false);// chưa xác minh
        creditRepo.save(cc);// lưu tín chỉ cacbon
        return cc;
    }

}
