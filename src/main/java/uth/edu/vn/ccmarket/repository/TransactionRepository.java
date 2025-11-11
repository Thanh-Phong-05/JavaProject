package uth.edu.vn.ccmarket.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import uth.edu.vn.ccmarket.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Dùng findAll(Pageable) để lấy bản ghi mới nhất trong service (sort theo id desc)
    Page<Transaction> findAll(Pageable pageable);
}
