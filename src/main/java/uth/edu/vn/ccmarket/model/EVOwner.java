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
    @Column(unique = true)
    private String username;
    private String password;
    private String email;

    double cashBalance = 0.0; // tiền mặt
    double creditBalance = 0.0; // tín chỉ
}
