package uth.edu.vn.ccmarket.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "ev_owner")
public class EVOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true) // tránh trùng tên đăng nhập
    private String username;
    private String password;
    private String email; // mục đích để xác minh hoặc tạo mk mới

    double cashBalance = 0.0; // tiền mặt sau khi bán tín chỉ cacbon
    double creditBalance = 0.0; // tín chỉ đổi từ CO2 tiết kiệm

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL) // 1 chủ xe - nhiều chuyến
    private List<Trip> trips = new ArrayList<>();

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL) // 1 chủ xe - 1 ví
    private Wallet wallet;

    public EVOwner() {
    }

    public EVOwner(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(double cashBalance) {
        this.cashBalance = cashBalance;
    }

    public double getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(double creditBalance) {
        this.creditBalance = creditBalance;
    }

    public void depositCredits(double q) {
        if (q > 0)
            this.creditBalance += q;
    }

    public boolean withdrawCredits(double q) {
        if (q <= 0)
            return false;
        if (q <= this.creditBalance) {
            this.creditBalance -= q;
            return true;
        }
        return false;
    }

    public void depositCash(double v) {
        if (v > 0)
            this.cashBalance += v;
    }

    public boolean withdrawCash(double v) {
        if (v <= 0)
            return false;
        if (v <= this.cashBalance) {
            this.cashBalance -= v;
            return true;
        }
        return false;
    }

}
