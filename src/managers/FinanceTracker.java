package managers;

import model.Expense;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FinanceTracker {
    private final List<Expense> expenses = new ArrayList<>();

    public void addExpense(Expense expense) {
        if (expense == null) {
            throw new IllegalArgumentException("Expense cannot be null");
        }
        expenses.add(expense);
    }

    public List<Expense> getExpenses() {
        return Collections.unmodifiableList(expenses);
    }

    public double getTotalIncome() {
        return expenses.stream()
                .filter(e -> e.getAmount() > 0)
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public double getTotalExpenses() {
        return expenses.stream()
                .filter(e -> e.getAmount() < 0)
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public double getBalance() {
        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }
}
