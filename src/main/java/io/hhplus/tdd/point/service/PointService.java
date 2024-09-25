package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.Entity.PointHistory;
import io.hhplus.tdd.point.Entity.UserPoint;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.service.manager.PointHistoryManager;
import io.hhplus.tdd.point.service.manager.PointManager;
import io.hhplus.tdd.point.service.reader.PointHistoryReader;
import io.hhplus.tdd.point.service.reader.PointReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointManager pointManager;
    private final PointHistoryManager pointHistoryManager;
    private final PointReader pointReader;
    private final PointHistoryReader pointHistoryReader;

    public UserPoint getUserPoint(long id) {
        return pointReader.read(id);
    }

    public List<PointHistory> getPointHistories(long id) {
        return pointHistoryReader.read(id);
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
