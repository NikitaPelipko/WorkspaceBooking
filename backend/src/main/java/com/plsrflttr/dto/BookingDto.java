package com.plsrflttr.dto;

import com.plsrflttr.models.BookingMode;
import com.plsrflttr.models.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private UUID id;

    private UUID userId;

    private BookingMode targetType;

    private UUID targetId;

    private String targetName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BookingStatus status;

    private Instant createdAt;
}