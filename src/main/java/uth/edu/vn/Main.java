package uth.edu.vn;

import uth.edu.vn.ccmarket.model.*;
import uth.edu.vn.ccmarket.service.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    // helper to read trips.csv
    public static List<Trip> readTripsFromCsv(String path) throws Exception {
        List<Trip> trips = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(",");
                LocalDate date = LocalDate.parse(parts[0].trim());
                double km = Double.parseDouble(parts[1].trim());
                trips.add(new Trip(date, km));
            }
        }
        return trips;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Carbon Credit Marketplace Demo ===");

        // create actors
        EVOwner alice = new EVOwner("Alice", "alice@example.com");
        CCBuyer corp = new CCBuyer("GreenCorp");
        CVA verifier = new CVA("CVA-1", "VerifierOne");
        // register in transaction service
        TransactionService txService = new TransactionService();
        txService.registerOwner(alice);
        txService.registerBuyer(corp);

        // services
        TripProcessorService tripService = new TripProcessorService();
        VerificationService verificationService = new VerificationService();
        PriceSuggestionService priceService = new PriceSuggestionService();
        MarketplaceService marketplace = new MarketplaceService(txService, priceService);

        // load trips
        String csv = "trips.csv";
        if (!Files.exists(Paths.get(csv))) {
            System.out.println("trips.csv not found. Creating sample file...");
            Files.write(Paths.get(csv),
                    Arrays.asList("date,distance_km", "2025-10-01,45.6", "2025-10-02,12.3", "2025-10-05,78.0"));
        }

        List<Trip> trips = readTripsFromCsv(csv);
        System.out.println("Loaded trips: " + trips);

        // process trips -> create credit (owner's wallet receives unverified credits)
        CarbonCredit credit = tripService.createCreditFromTrips(alice, trips);
        System.out.println("Created credit: " + credit);
        marketplace.registerCredit(credit);

        // CVA verifies the credit
        boolean ok = verificationService.verifyCredit(verifier, credit);
        if (!ok) {
            System.out.println("Credit verification failed. Exiting.");
            return;
        }

        // Alice wants to list all her credit at fixed price
        double suggested = priceService.suggestPricePerCredit(marketplace.getCompletedTransactions());
        System.out.printf("Suggested price per credit: %.2f%n", suggested);

        // create listing for ALL credit (use fixed price)
        Listing listing = marketplace.createListing(credit, alice, credit.getQuantity(), Listing.Type.FIXED_PRICE,
                suggested);
        System.out.println("Listing available: " + listing);

        // simulate buyer deposits cash
        corp.getWallet().depositCash(1000.0);
        txService.registerBuyer(corp);

        // buyer searches and buys
        List<Listing> found = marketplace.searchListings(null, null);
        System.out.println("Found listings: " + found);

        Transaction tx = marketplace.buyFixedPrice(corp, listing);
        System.out.println("Buyer wallet after purchase: cash=" + corp.getWallet().getCashBalance());
        System.out.println("Seller wallet after sale: cash=" + alice.getWallet().getCashBalance());

        // Report: owner personal report
        System.out.println("=== Owner Report ===");
        double totalCO2 = trips.stream().mapToDouble(Trip::getDistanceKm).sum() * (0.25 - 0.05) / 1000.0;
        System.out.printf("Owner %s reduced total CO2 (tonnes): %.4f%n", alice.getName(), totalCO2);
        System.out.printf("Revenue from credits: %.2f%n", alice.getWallet().getCashBalance());
        System.out.printf("Remaining credits in wallet: %.4f%n", alice.getWallet().getCreditBalance());

        // Platform-level report
        System.out.println("=== Platform transactions ===");
        for (Transaction t : marketplace.getCompletedTransactions()) {
            System.out.println(t);
        }

        System.out.println("Demo finished.");
    }
}
