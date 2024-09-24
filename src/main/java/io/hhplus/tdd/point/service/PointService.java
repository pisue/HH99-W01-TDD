package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.Entity.PointHistory;
import io.hhplus.tdd.point.Entity.UserPoint;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.service.manager.PointHistoryManager;
import io.hhplus.tdd.point.service.manager.PointManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;
    private final PointManager pointManager;
    private final PointHistoryManager pointHistoryManager;

    public UserPoint getUserPoint(long id) {
        return userPointTable.selectById(id);
    }

    public List<PointHistory> getPointHistories(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

    public UserPoint charge(long id, long amount) {
        final UserPoint updatedUserPoint = pointManager.charge(id, amount);
        pointHistoryManager.append(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
        return updatedUserPoint;
    }

    public UserPoint use(long id, long amount) {
        final UserPoint updatedUserPoint = pointManager.use(id, amount);
        pointHistoryManager.append(id, amount, TransactionType.USE, System.currentTimeMillis());
        return updatedUserPoint;
    }
}
