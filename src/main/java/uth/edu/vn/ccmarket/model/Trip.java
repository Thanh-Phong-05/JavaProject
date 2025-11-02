package uth.edu.vn.ccmarket.model;

import java.time.LocalDate;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private double distanceKm;
    private double energyConsumedKWh;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private EVOwner owner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double getEnergyConsumedKWh() {
        return energyConsumedKWh;
    }

    public void setEnergyConsumedKWh(double energyConsumedKWh) {
        this.energyConsumedKWh = energyConsumedKWh;
    }

    public EVOwner getOwner() {
        return owner;
    }

    public void setOwner(EVOwner owner) {
        this.owner = owner;
    }

}
