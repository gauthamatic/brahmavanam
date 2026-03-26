package com.brahmavanam.calendar.service;

import com.brahmavanam.calendar.model.Event;
import com.brahmavanam.calendar.model.User;
import com.brahmavanam.calendar.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CalendarService calendarService;

    private User user;

    // CalendarService does .substring(0, 23) so date strings must be at least 23 chars
    // matching the format the frontend sends: "2026-03-09T00:00:00.000"
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private String toDateString(LocalDate date) {
        return date.atStartOfDay().format(FORMATTER);
    }

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmailId("john@example.com");
        user.setPassword("password");
    }

    private Event buildEvent(LocalDate startDate, LocalDate endDate) {
        Event event = new Event();
        event.setTitle("Test Event");
        event.setStartDate(toDateString(startDate));
        event.setEndDate(toDateString(endDate));
        event.setUser(user);
        return event;
    }

    // ─── saveEventDetails ────────────────────────────────────────────────────

    @Test
    void saveEvent_pastDate_throwsException() {
        Event event = buildEvent(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calendarService.saveEventDetails(event));

        assertTrue(ex.getMessage().contains("live in the past"));
    }

    @Test
    void saveEvent_dateAlreadyBooked_throwsException() {
        Event event = buildEvent(LocalDate.now().plusDays(5), LocalDate.now().plusDays(7));

        when(eventRepository.findByStartDate(event.getStartDate()))
                .thenReturn(List.of(new Event()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calendarService.saveEventDetails(event));

        assertTrue(ex.getMessage().contains("Non-dual"));
    }

    @Test
    void saveEvent_userHasActiveBooking_throwsException() {
        Event event = buildEvent(LocalDate.now().plusDays(10), LocalDate.now().plusDays(12));

        Event existingEvent = new Event();
        existingEvent.setStartDate(toDateString(LocalDate.now().plusDays(5)));

        when(eventRepository.findByStartDate(event.getStartDate())).thenReturn(Collections.emptyList());
        when(eventRepository.findByUserId(user.getId())).thenReturn(List.of(existingEvent));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calendarService.saveEventDetails(event));

        assertTrue(ex.getMessage().contains("Active booking found"));
    }

    @Test
    void saveEvent_within90DaysOfPreviousEvent_throwsException() {
        Event event = buildEvent(LocalDate.now().plusDays(10), LocalDate.now().plusDays(12));

        Event existingEvent = new Event();
        existingEvent.setStartDate(toDateString(LocalDate.now().minusDays(30)));

        when(eventRepository.findByStartDate(event.getStartDate())).thenReturn(Collections.emptyList());
        when(eventRepository.findByUserId(user.getId())).thenReturn(List.of(existingEvent));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calendarService.saveEventDetails(event));

        assertTrue(ex.getMessage().contains("90 days"));
    }

    @Test
    void saveEvent_oneDayBooking_throwsException() {
        LocalDate start = LocalDate.now().plusDays(100);
        Event event = buildEvent(start, start.plusDays(1));

        when(eventRepository.findByStartDate(event.getStartDate())).thenReturn(Collections.emptyList());
        when(eventRepository.findByUserId(user.getId())).thenReturn(Collections.emptyList());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calendarService.saveEventDetails(event));

        assertTrue(ex.getMessage().contains("A day is not enough"));
    }

    @Test
    void saveEvent_validEvent_savesSuccessfully() {
        LocalDate start = LocalDate.now().plusDays(100);
        Event event = buildEvent(start, start.plusDays(3));

        when(eventRepository.findByStartDate(event.getStartDate())).thenReturn(Collections.emptyList());
        when(eventRepository.findByUserId(user.getId())).thenReturn(Collections.emptyList());
        when(eventRepository.save(event)).thenReturn(event);

        Event saved = calendarService.saveEventDetails(event);

        assertNotNull(saved);
        verify(eventRepository, times(1)).save(event);
    }

    // ─── deleteEvent ─────────────────────────────────────────────────────────

    @Test
    void deleteEvent_notFound_throwsException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> calendarService.deleteEvent(1L, user.getId()));
    }

    @Test
    void deleteEvent_pastEvent_throwsException() {
        Event event = buildEvent(LocalDate.now().minusDays(5), LocalDate.now().minusDays(3));
        event.setUser(user);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calendarService.deleteEvent(1L, user.getId()));

        assertTrue(ex.getMessage().contains("past belongs to past"));
    }

    @Test
    void deleteEvent_differentUser_returnsFalse() {
        Event event = buildEvent(LocalDate.now().plusDays(10), LocalDate.now().plusDays(12));
        event.setUser(user);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        boolean result = calendarService.deleteEvent(1L, 99); // different user id

        assertFalse(result);
        verify(eventRepository, never()).delete(any());
    }

    @Test
    void deleteEvent_validRequest_deletesAndReturnsTrue() {
        Event event = buildEvent(LocalDate.now().plusDays(10), LocalDate.now().plusDays(12));
        event.setUser(user);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        boolean result = calendarService.deleteEvent(1L, user.getId());

        assertTrue(result);
        verify(eventRepository, times(1)).delete(event);
    }
}
