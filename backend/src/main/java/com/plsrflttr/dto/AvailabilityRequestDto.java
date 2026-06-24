package com.plsrflttr.dto;

import com.plsrflttr.models.BookingMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityRequestDto {
    private Set<UUID> targetIds;
    private BookingMode targetType;
    private String startTime;
    private String endTime;
}
