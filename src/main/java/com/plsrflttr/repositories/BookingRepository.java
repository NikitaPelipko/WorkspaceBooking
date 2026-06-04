package com.plsrflttr.repositories;

import com.plsrflttr.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByUserId(UUID userId);

    List<Booking> findByWorkplaceId(UUID workplaceId);

    List<Booking> findByWorkplaceIdAndStartTimeLessThanAndEndTimeGreaterThan(
            UUID workplaceId,
            LocalDateTime endTime,
            LocalDateTime startTime
    );
}

