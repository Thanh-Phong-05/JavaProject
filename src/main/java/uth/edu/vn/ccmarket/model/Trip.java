package uth.edu.vn.ccmarket.model;

import java.time.LocalDate;

public class Trip {
    private LocalDate date;
    private double distanceKm;

    public Trip(LocalDate date, double distanceKm) {
        this.date = date;
        this.distanceKm = distanceKm;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    @Override
    public String toString() {
        return "Trip{" + "date=" + date + ", distanceKm=" + distanceKm + '}';
    }
}
