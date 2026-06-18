package com.plsrflttr.mappers;

import com.plsrflttr.dto.BookingDto;
import com.plsrflttr.models.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingMapper {

    @Mapping(target = "userId", source = "user.id")
    BookingDto toDto(Booking booking);
}