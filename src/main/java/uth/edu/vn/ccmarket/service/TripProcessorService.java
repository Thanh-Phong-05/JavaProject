package uth.edu.vn.ccmarket.service;

import uth.edu.vn.ccmarket.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TripProcessorService {
    private static final double CO2_CHANGE = 0.120;

    public double calculateCO2Saved(double distanceKm) {
        return distanceKm * CO2_CHANGE;
    }

    public List<Trip> loadTripsFromFile(String filePath, EVOwner owner) throws IOException {
        List<Trip> trips = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                double distance = Double.parseDouble(parts[0]);
                double energy = Double.parseDouble(parts[1]);
                LocalDate date = LocalDate.parse(parts[2]);
                Trip trip = new Trip();
                trip.setDistanceKm(distance);
                trip.setEnergyConsumedKWh(energy);
                trip.setDate(date);
                trip.setOwner(owner);
                trips.add(trip);
            }
        }
        return trips;
    }
}
