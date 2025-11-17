package uth.edu.vn.ccmarket.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import uth.edu.vn.ccmarket.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findAll(Pageable pageable);

    // tìm giao dịch theo ng mua
    List<Transaction> findBySellerId(Long sellerId);
}
