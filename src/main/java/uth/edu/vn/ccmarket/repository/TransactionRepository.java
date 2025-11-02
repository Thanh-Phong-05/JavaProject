package uth.edu.vn.ccmarket.repository;

import uth.edu.vn.ccmarket.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
