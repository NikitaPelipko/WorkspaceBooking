package com.plsrflttr.dto;

import com.plsrflttr.models.BookingMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {
    private UUID targetId;

    private BookingMode targetType;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
