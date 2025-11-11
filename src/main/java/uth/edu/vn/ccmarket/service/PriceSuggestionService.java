package uth.edu.vn.ccmarket.service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import uth.edu.vn.ccmarket.model.Transaction;
import uth.edu.vn.ccmarket.repository.TransactionRepository;

@Service
public class PriceSuggestionService {

    private final TransactionRepository transactionRepository;

    @Value("${app.ai.suggest-sample-size:50}")
    private int sampleSize;

    public PriceSuggestionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public double suggestPricePerCredit() {
        // lấy N bản ghi mới nhất theo id desc
        List<Transaction> latest = transactionRepository
                .findAll(PageRequest.of(0, Math.max(sampleSize, 1), Sort.by(Sort.Direction.DESC, "id")))
                .getContent();

        double baseline = 10.0;
        if (latest.isEmpty()) return baseline;

        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;

        for (Transaction tx : latest) {
            if (!isCompleted(tx)) continue;

            BigDecimal total = getBigDecimal(tx, "getTotalAmount");
            BigDecimal credits = getBigDecimal(tx, "getCreditAmount");
            if (total == null || credits == null) continue;
            if (credits.compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal ppc = total.divide(credits, 6, RoundingMode.HALF_UP);
            sum = sum.add(ppc);
            count++;
        }

        if (count == 0) return baseline;
        return sum.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP).doubleValue();
    }

    // --- Reflection helpers ---

    private boolean isCompleted(Transaction tx) {
        // Ưu tiên: tx.getStatus().toString().equals("COMPLETED")
        Object status = invokeGetter(tx, "getStatus");
        if (status == null) return true; // nếu không có status, tạm coi hợp lệ để không chặn tính năng

        String s = status.toString();
        return "COMPLETED".equalsIgnoreCase(s);
    }

    private BigDecimal getBigDecimal(Transaction tx, String methodName) {
        Object val = invokeGetter(tx, methodName);
        if (val instanceof BigDecimal) return (BigDecimal) val;

        if (val instanceof Number) {
            return new BigDecimal(val.toString());
        }
        return null;
    }

    private Object invokeGetter(Object target, String methodName) {
        try {
            Method m = target.getClass().getMethod(methodName);
            m.setAccessible(true);
            return m.invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }
}
