package com.plsrflttr.repositories;

import com.plsrflttr.models.Booking;
import com.plsrflttr.models.BookingMode;
import com.plsrflttr.models.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByUserIdAndStatusNot(UUID userId, BookingStatus status);

    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.targetId = :targetId
              AND b.targetType = :targetType
              AND b.status = 'CONFIRMED'
              AND b.startTime < :endTime
              AND b.endTime > :startTime
            """)
    boolean existsOverlapping(
            @Param("targetId") UUID targetId,
            @Param("targetType") BookingMode targetType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    @Query("""
    select b
    from Booking b
    where b.status = com.plsrflttr.models.BookingStatus.CONFIRMED
      and b.startTime < :to
      and b.endTime > :from
      and b.targetId in :targetIds
""")
    List<Booking> findBusyTargets(
            @Param("targetIds") Set<UUID> targetIds,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}