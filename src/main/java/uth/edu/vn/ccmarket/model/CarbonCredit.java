package uth.edu.vn.ccmarket.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "carbon_credit")
public class CarbonCredit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) // nhiều tín chỉ - 1 chủ xe
    @JoinColumn(name = "owner_id", nullable = false)
    private EVOwner owner;
}
