package uth.edu.vn.ccmarket.repository;

import uth.edu.vn.ccmarket.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

// THÊM:
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // THÊM: lấy các giao dịch hoàn tất mới nhất
    List<Transaction> findByStatusOrderByIdDesc(Transaction.Status status, Pageable pageable);
}
