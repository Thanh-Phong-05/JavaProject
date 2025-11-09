package uth.edu.vn.ccmarket.model;

import jakarta.persistence.*;

@Entity
public class CCBuyer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // tài khoản riêng
    private String username;
    private String password;
    private String email;

    private String organizationType;

    @OneToOne(mappedBy = "buyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Wallet wallet;

    public CCBuyer() {
    }

    public CCBuyer(String username, String password, String email, String organizationType) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.organizationType = organizationType;
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

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}