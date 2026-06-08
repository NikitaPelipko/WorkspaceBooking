package com.plsrflttr.controllers;

import com.plsrflttr.dto.BookingDto;
import com.plsrflttr.dto.BookingRequestDto;
import com.plsrflttr.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/booking")
public class BookingsController {
    private final BookingService bookingService;

    @PostMapping("/")
    public ResponseEntity<BookingDto> createBooking(@RequestBody BookingRequestDto bookingRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(bookingRequest));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable("bookingId") UUID bookingId) {
            return ResponseEntity.status(HttpStatus.OK).body(bookingService.getBooking(bookingId));
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<BookingDto> cancelBooking(@PathVariable("bookingId") UUID bookingId) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.cancelBooking(bookingId));

    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingDto>> getMyBookings() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookingService.getMyBookings());
    }

    @GetMapping("/rooms/{room_id}/availability")
    public ResponseEntity<Boolean> checkRoomAvailability(
            @PathVariable UUID room_id,
            @RequestParam String startTime,
            @RequestParam String endTime
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookingService.checkRoomAvailability(
                        room_id,
                        startTime,
                        endTime
                ));
    }

    @GetMapping("/workplaces/{workplace_id}/availability")
    public ResponseEntity<Boolean> checkWorkplaceAvailability(
            @PathVariable UUID workplace_id,
            @RequestParam String startTime,
            @RequestParam String endTime
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookingService.checkWorkplaceAvailability(
                        workplace_id,
                        startTime,
                        endTime
                ));
    }

}
