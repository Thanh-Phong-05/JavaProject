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

}
