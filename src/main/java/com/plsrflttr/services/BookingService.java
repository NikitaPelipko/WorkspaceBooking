package com.plsrflttr.services;

import com.plsrflttr.dto.BookingDto;
import com.plsrflttr.dto.BookingRequestDto;
import com.plsrflttr.mappers.BookingMapper;
import com.plsrflttr.models.*;
import com.plsrflttr.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final WorkplaceRepository workplaceRepository;
    private final RoomRepository roomRepository;
    private final BookingMapper bookingMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


    @Transactional
    public BookingDto createBooking(BookingRequestDto request) {
        validateTimeRange(request.getStartTime(), request.getEndTime());

        User currentUser = getCurrentUser();

        switch (request.getTargetType()) {
            case WORKPLACE -> validateWorkplaceBooking(request);
            case ROOM -> validateRoomBooking(request);
        }

        boolean overlaps = bookingRepository.existsOverlapping(
                request.getTargetId(),
                request.getTargetType(),
                request.getStartTime(),
                request.getEndTime()
        );
        if (overlaps) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This time slot is already booked"
            );
        }

        Booking booking = new Booking();
        booking.setUser(currentUser);
        booking.setTargetType(request.getTargetType());
        booking.setTargetId(request.getTargetId());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking saved = bookingRepository.save(booking);
        return enrichWithTargetName(bookingMapper.toDto(saved));
    }


    public BookingDto getBooking(UUID bookingId) {
        Booking booking = findBookingById(bookingId);
        ensureOwnerOrAdmin(booking);
        return enrichWithTargetName(bookingMapper.toDto(booking));
    }


    @Transactional
    public BookingDto cancelBooking(UUID bookingId) {
        Booking booking = findBookingById(bookingId);
        ensureOwnerOrAdmin(booking);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Booking is already cancelled"
            );
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Can't cancel this Booking"
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return enrichWithTargetName(bookingMapper.toDto(bookingRepository.save(booking)));
    }



    public List<BookingDto> getMyBookings() {
        User currentUser = getCurrentUser();
        return bookingRepository
                .findByUserIdAndStatusNot(currentUser.getId(), BookingStatus.CANCELLED)
                .stream()
                .map(bookingMapper::toDto)
                .map(this::enrichWithTargetName)
                .toList();
    }


    public boolean checkRoomAvailability(UUID roomId, String startTimeStr, String endTimeStr) {
        LocalDateTime start = parseDateTime(startTimeStr);
        LocalDateTime end = parseDateTime(endTimeStr);
        validateTimeRange(start, end);

        roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workplace not found"));

        return !bookingRepository.existsOverlapping(roomId, BookingMode.ROOM, start, end);
    }

    public boolean checkWorkplaceAvailability(UUID workplaceId, String startTimeStr, String endTimeStr) {
        LocalDateTime start = parseDateTime(startTimeStr);
        LocalDateTime end = parseDateTime(endTimeStr);
        validateTimeRange(start, end);

        Workplace workplace = workplaceRepository.findById(workplaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workplace not found"));

        if (workplace.getStatus() != WorkplaceStatus.AVAILABLE) {
            return false;
        }

        return !bookingRepository.existsOverlapping(workplaceId, BookingMode.WORKPLACE, start, end);
    }



    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found"
                ));
    }


    private void validateRoomBooking(BookingRequestDto request) {
        Room room = roomRepository.findById(request.getTargetId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Room not found"
                ));

        if (room.getBookingMode() != BookingMode.ROOM) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This room doesn't support room booking mode"
            );
        }
    }


    private void validateWorkplaceBooking(BookingRequestDto request) {
        Workplace workplace = workplaceRepository.findById(request.getTargetId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Workplace not found"
                ));

        if (workplace.getStatus() != WorkplaceStatus.AVAILABLE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Workplace is unavailable"
            );
        }


        Room room = workplace.getRoom();
        if (room != null && room.getBookingMode() != BookingMode.WORKPLACE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This room doesn't support workplace booking mode"
            );
        }
    }


    private void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "time can't be null"
            );
        }
        if (!start.isBefore(end)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "start time can't be before end time"
            );
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "start time can't be before current time"
            );
        }
    }


    private void ensureOwnerOrAdmin(Booking booking) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) return;

        String currentEmail = auth.getName();
        if (!booking.getUser().getEmail().equals(currentEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied"
            );
        }
    }

    private BookingDto enrichWithTargetName(BookingDto dto) {
        if (dto.getTargetId() == null || dto.getTargetType() == null) return dto;

        switch (dto.getTargetType()) {
            case ROOM -> roomRepository.findById(dto.getTargetId())
                    .ifPresent(room -> dto.setTargetName(room.getName()));
            case WORKPLACE -> workplaceRepository.findById(dto.getTargetId())
                    .ifPresent(wp -> dto.setTargetName(wp.getCode()));
        }
        return dto;
    }

    private Booking findBookingById(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found"
                ));
    }

    private LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Wrong date format"
            );
        }
    }
}