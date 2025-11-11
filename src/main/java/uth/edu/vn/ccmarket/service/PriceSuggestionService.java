package uth.edu.vn.ccmarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uth.edu.vn.ccmarket.domain.Transaction;
import uth.edu.vn.ccmarket.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceSuggestionService {

    private final TransactionRepository transactionRepository;

    @Value("${app.ai.suggest-sample-size:50}")
    private int sampleSize;

    /**
     * Gợi ý giá bán/credit dựa trên trung bình các giao dịch COMPLETED gần nhất.
     * Nếu chưa có dữ liệu thì trả baseline 10.0
     */
    public double suggestPricePerCredit() {
        List<Transaction> latest = transactionRepository.findByStatusOrderByIdDesc(
                Transaction.Status.COMPLETED, PageRequest.of(0, Math.max(sampleSize, 1)));

        double baseline = 10.0;
        if (latest.isEmpty()) return baseline;

        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;

        for (Transaction t : latest) {
            if (t.getCreditAmount() == null || t.getCreditAmount().compareTo(BigDecimal.ZERO) <= 0) continue;
            if (t.getTotalAmount() == null || t.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal ppc = t.getTotalAmount().divide(t.getCreditAmount(), 6, RoundingMode.HALF_UP);
            sum = sum.add(ppc);
            count++;
        }

        if (count == 0) return baseline;

        return sum.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP).doubleValue();
    }
}
