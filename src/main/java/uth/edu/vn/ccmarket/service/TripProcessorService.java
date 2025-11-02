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

}
