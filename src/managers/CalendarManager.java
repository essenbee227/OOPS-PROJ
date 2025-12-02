package managers;

import model.Event;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarManager {
    private final List<Event> events = new ArrayList<>();

    public void addEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        events.add(event);
    }

    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public List<Event> getEventsBetween(LocalDate start, LocalDate end) {
        return events.stream()
                .filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end))
                .collect(Collectors.toList());
    }

    public double getTotalExpectedCostBetween(LocalDate start, LocalDate end) {
        return getEventsBetween(start, end).stream()
                .mapToDouble(Event::getExpectedCost)
                .sum();
    }
}
