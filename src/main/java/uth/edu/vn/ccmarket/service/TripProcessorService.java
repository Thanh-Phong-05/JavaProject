package uth.edu.vn.ccmarket.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uth.edu.vn.ccmarket.model.CarbonCredit;
import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.model.Trip;
import uth.edu.vn.ccmarket.repository.CarbonCreditRepository;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import uth.edu.vn.ccmarket.repository.TripRepository;

@Service
public class TripProcessorService {

    // GIỮ: 1 credit = 1 1000kg CO2e (demo ban đầu của bạn)
    private static final double KG_PER_TONNE = 1000.0;

    // ĐỔI: từ hằng số sang cấu hình (gram CO2/km tiết kiệm)
    @Value("${app.carbon.gram_per_km_saving:120}")
    private double savedGramPerKm;

    private final TripRepository tripRepo;
    private final CarbonCreditRepository creditRepo;
    private final EVOwnerRepository ownerRepo;

    public TripProcessorService(TripRepository tripRepo, CarbonCreditRepository creditRepo,
            EVOwnerRepository ownerRepo) {
        this.tripRepo = tripRepo;
        this.creditRepo = creditRepo;
        this.ownerRepo = ownerRepo;
    }

    /** parse CSV (giữ nguyên logic của bạn, chỉ refactor nhẹ) */
    @Transactional
    public List<Trip> parseTripsCsv(InputStream csvInput, EVOwner owner) throws IOException {
        List<Trip> trips = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvInput))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (first) {
                    first = false;
                    if (!startsWithDigit(line)) {
                        // header -> skip
                        continue;
                    }
                }

                String[] parts = line.split(",");
                if (parts.length == 0) continue;

                for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();

                Trip t = parseRowFlexible(parts, owner);
                if (t == null) continue;
                if (t.getDistanceKm() < 0) continue;

                trips.add(t);
            }
        }

        if (trips.isEmpty()) return List.of();
        return tripRepo.saveAll(trips);
    }

    @Transactional
    public CarbonCredit createCreditFromTrips(EVOwner owner, List<Trip> trips) {
        double totalKm = trips.stream().mapToDouble(Trip::getDistanceKm).sum();

        // gram -> kg
        double savedKg = (totalKm * savedGramPerKm) / 1000.0;
        double tonnes = savedKg / KG_PER_TONNE;

        if (tonnes <= 0) {
            throw new IllegalArgumentException("Không đủ dữ liệu để tạo tín chỉ (tonnes <= 0)");
        }

        CarbonCredit cc = new CarbonCredit(owner, tonnes);
        cc.setVerified(false);

        owner.getWallet().depositCredits(tonnes);
        ownerRepo.save(owner);

        return creditRepo.save(cc);
    }

    private static boolean startsWithDigit(String s) {
        if (s == null || s.isEmpty()) return false;
        char c = s.charAt(0);
        return c >= '0' && c <= '9';
    }

    private Trip parseRowFlexible(String[] parts, EVOwner owner) {
        try {
            if (parts.length >= 3) {
                if (looksLikeDate(parts[0])) {
                    LocalDate date = LocalDate.parse(parts[0]);
                    double distanceKm = parseDoubleSafe(parts[1]);
                    double energy = parseOptionalDouble(parts, 2);
                    return new Trip(distanceKm, energy, date, owner);
                } else {
                    double distanceKm = parseDoubleSafe(parts[0]);
                    double energy = parseOptionalDouble(parts, 1);
                    LocalDate date = looksLikeDate(parts[2]) ? LocalDate.parse(parts[2]) : LocalDate.now();
                    return new Trip(distanceKm, energy, date, owner);
                }
            } else if (parts.length == 2) {
                if (looksLikeDate(parts[0])) {
                    LocalDate date = LocalDate.parse(parts[0]);
                    double distanceKm = parseDoubleSafe(parts[1]);
                    return new Trip(distanceKm, 0.0, date, owner);
                } else if (looksLikeDate(parts[1])) {
                    double distanceKm = parseDoubleSafe(parts[0]);
                    LocalDate date = LocalDate.parse(parts[1]);
                    return new Trip(distanceKm, 0.0, date, owner);
                } else {
                    return null;
                }
            } else if (parts.length == 1) {
                double distanceKm = parseDoubleSafe(parts[0]);
                return new Trip(distanceKm, 0.0, LocalDate.now(), owner);
            }
        } catch (Exception ignore) {
            return null;
        }
        return null;
    }

    private static boolean looksLikeDate(String s) {
        return s != null && s.length() >= 10 && s.charAt(4) == '-' && s.charAt(7) == '-';
    }

    private static double parseDoubleSafe(String s) {
        if (s == null || s.isBlank()) return 0.0;
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return 0.0; }
    }

    private static double parseOptionalDouble(String[] parts, int idx) {
        if (idx < parts.length && !parts[idx].isBlank()) {
            return parseDoubleSafe(parts[idx]);
        }
        return 0.0;
    }
}
