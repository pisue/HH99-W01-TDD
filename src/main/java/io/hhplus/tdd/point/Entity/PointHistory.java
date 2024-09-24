package io.hhplus.tdd.point.Entity;

import io.hhplus.tdd.point.TransactionType;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
}
