package io.hhplus.tdd.point.service.manager;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.Entity.UserPoint;
import io.hhplus.tdd.point.exception.PointException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointManager {
    private final UserPointTable userPointTable;

    public UserPoint charge(Long id, Long amount) {
        UserPoint existingUserPoint = userPointTable.selectById(id);
        if (amount <= 0) {
            throw new PointException("적립할 포인트는 0보다 커야 합니다.");
        }
        //업데이트
        return userPointTable.insertOrUpdate(id, existingUserPoint.point() + amount);
    }

    public UserPoint use(long id, long amount) {
        UserPoint existingUserPoint = userPointTable.selectById(id);
        if (amount <= 0) {
            throw new PointException("사용할 포인트는 0보다 커야 합니다.");
        }
        if (existingUserPoint.point() < amount) {
            throw new PointException("사용할 포인트가 부족합니다.");
        }
        return userPointTable.insertOrUpdate(id, existingUserPoint.point() - amount);
    }
}
