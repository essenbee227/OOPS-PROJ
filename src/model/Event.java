package model;

import java.time.LocalDate;

public class Event {
    private String title;
    private LocalDate date;
    private double expectedCost;
    private String category;

    public Event(String title, LocalDate date, double expectedCost, String category) {
        this.title = title;
        this.date = date;
        this.expectedCost = expectedCost;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getExpectedCost() {
        return expectedCost;
    }

    public void setExpectedCost(double expectedCost) {
        this.expectedCost = expectedCost;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
