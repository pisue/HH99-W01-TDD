package io.hhplus.tdd.point.service.manager;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointHistoryManager {
    private PointHistoryTable pointHistoryTable;

    public void append(long id, long amount, TransactionType type, long updateMillis) {
        pointHistoryTable.insert(id, amount, type, updateMillis);
    }
}
