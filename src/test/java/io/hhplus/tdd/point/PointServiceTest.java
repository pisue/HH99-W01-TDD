package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.Entity.PointHistory;
import io.hhplus.tdd.point.Entity.UserPoint;
import io.hhplus.tdd.point.exception.PointException;
import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.service.manager.PointHistoryManager;
import io.hhplus.tdd.point.service.manager.PointManager;
import io.hhplus.tdd.point.service.reader.PointHistoryReader;
import io.hhplus.tdd.point.service.reader.PointReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

    @Mock
    private PointReader pointReader;

    @Mock
    private PointHistoryReader pointHistoryReader;

    @Mock
    private PointManager pointManager;


    @Mock
    private PointHistoryManager pointHistoryManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("존재하는 사용자면 포인트를 리턴한다")
    void getUserPoint_shouldReturnUserPoint_whenUserExists() {
        // Given
        long userId = 1L;
        UserPoint expectedUserPoint = new UserPoint(userId, 1000L, System.currentTimeMillis());
        when(pointReader.read(userId)).thenReturn(expectedUserPoint);

        // When
        UserPoint result = pointService.getUserPoint(userId);

        // Then
        assertEquals(expectedUserPoint, result);
        verify(pointReader, times(1)).read(userId);
    }

    @Test
    @DisplayName("존재하지 않는 사용자면 빈 포인트를 리턴한다")
    void getUserPoint_shouldReturnEmptyUserPoint_whenUserDoesNotExist() {
        // Given
        long userId = 2L;
        UserPoint emptyUserPoint = UserPoint.empty(userId);
        when(pointReader.read(userId)).thenReturn(emptyUserPoint);

        // When
        UserPoint result = pointService.getUserPoint(userId);

        // Then
        assertEquals(emptyUserPoint, result);
        verify(pointReader, times(1)).read(userId);
    }

    @Test
    @DisplayName("사용자의 포인트 내역 조회")
    void getPointHistory_shouldReturnPointHistoryList_whenPointHistoryExists() {
        //Given
        long userId = 1L;
        long chargeAmount = 1000L;
        long useAmount = 500L;
        List<PointHistory> expectedPointHistories = List.of(
                new PointHistory(1L, userId, chargeAmount, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, userId, useAmount, TransactionType.USE, System.currentTimeMillis())
        );
        when(pointHistoryReader.read(userId)).thenReturn(expectedPointHistories);

        //when
        List<PointHistory> result = pointService.getPointHistories(userId);

        //then
        assertEquals(expectedPointHistories, result);
        verify(pointHistoryReader, times(1)).read(userId);

    }

    @Test
    @DisplayName("사용자의 포인트 내역이 존재하지 않을 때")
    void getPointHistory_shouldReturnNull_whenPointHistoryDoesNotExists() {
        //Given
        long userId = 1L;
        List<PointHistory> expectedPointHistories = List.of();
        when(pointService.getPointHistories(userId)).thenReturn(expectedPointHistories);
        when(pointHistoryReader.read(userId)).thenThrow(new PointException("포인트 내역이 존재하지 않습니다."));

        //when
        PointException exception = assertThrows(PointException.class, () -> pointService.getPointHistories(userId));


        //then
        verify(pointHistoryReader, times(1)).read(userId);

        assertEquals("포인트 내역이 존재하지 않습니다.", exception.getMessage());

    }

    @Test
    @DisplayName("사용자의 포인트를 충전합니다")
    void charge_shouldUpdateUserPoint() {
        //Given
        long userId = 1L;
        long amount = 500L;
        UserPoint existUserPoint = new UserPoint(userId, 0L, System.currentTimeMillis());
        long existAmount = existUserPoint.point();
        long updateAmount = amount + existAmount;

        UserPoint expectedUserPoint = new UserPoint(userId, updateAmount, System.currentTimeMillis());
        when(pointReader.read(userId)).thenReturn(existUserPoint);
        when(pointManager.charge(userId, updateAmount)).thenReturn(expectedUserPoint);

        //when
        UserPoint result = pointService.charge(userId, amount);

        //then
        verify(pointManager, times(1)).charge(userId, updateAmount);
        assertEquals(expectedUserPoint, result);

    }

    @Test
    @DisplayName("포인트를 사용합니다.")
    void use_shouldUpdateUserPoint() {
        //given
        long userId = 1L;
        long useAmount = 500L;
        UserPoint expectedUserPoint = new UserPoint(userId, 0L, System.currentTimeMillis());
        when(pointManager.use(userId, useAmount)).thenReturn(expectedUserPoint);

        //when
        UserPoint result = pointService.use(userId, useAmount);

        //then
        verify(pointManager, times(1))
                .use(eq(userId), eq(useAmount));
        verify(pointHistoryManager, times(1))
                .append(eq(userId), eq(useAmount), eq(TransactionType.USE), anyLong());
        assertEquals(expectedUserPoint, result);
    }

    @Test
    @DisplayName("유저 포인트보다 사용핲 포인트가 많을 때 예외")
    void use_shouldNotUpdateUserPoint() {
        //given
        long userId = 1L;
        long useAmount = 500L;
        when(pointManager.use(userId, useAmount)).thenThrow(new PointException("사용할 포인트가 부족합니다."));

        //when
        PointException exception = assertThrows(PointException.class, () -> pointService.use(userId, useAmount));

        verify(pointManager, times(1)).use(userId, useAmount);
        verify(pointHistoryManager, never()).append(anyLong(), anyLong(), any(), anyLong());

        //then
        assertEquals("사용할 포인트가 부족합니다.", exception.getMessage());
    }
}