package uth.edu.vn.ccmarket.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ev_owner")
public class EVOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    private String email;

    private double cashBalance = 0.0;
    private double creditBalance = 0.0;

    public EVOwner() {
    }

    public EVOwner(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // getters & setters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String u) {
        this.username = u;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String p) {
        this.password = p;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String e) {
        this.email = e;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public void depositCash(double v) {
        this.cashBalance += v;
    }

    public double getCreditBalance() {
        return creditBalance;
    }

    public void depositCredits(double q) {
        this.creditBalance += q;
    }

    public boolean withdrawCredits(double q) {
        if (q <= creditBalance) {
            creditBalance -= q;
            return true;
        }
        return false;
    }
}
