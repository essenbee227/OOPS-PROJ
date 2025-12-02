package model;

import java.time.LocalDate;

public class Expense {
    private double amount;
    private LocalDate date;
    private String type;
    private String description;

    public Expense(double amount, LocalDate date, String type, String description) {
        setAmount(amount);
        this.date = date;
        this.type = type;
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount == 0) {
            throw new IllegalArgumentException("Amount cannot be zero.");
        }
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
