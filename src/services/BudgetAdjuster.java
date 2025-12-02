package services;

import managers.CalendarManager;
import managers.FinanceTracker;

import java.time.LocalDate;
import java.time.YearMonth;

public class BudgetAdjuster {

    public double calculateDailyLimit(FinanceTracker financeTracker,
                                      CalendarManager calendarManager,
                                      LocalDate today,
                                      YearMonth month) {
        double balance = financeTracker.getBalance();
        LocalDate start = today;
        LocalDate end = month.atEndOfMonth();
        double upcomingCosts = calendarManager.getTotalExpectedCostBetween(start, end);

        double available = balance - upcomingCosts;
        if (available <= 0) {
            return 0;
        }

        int remainingDays = end.getDayOfMonth() - today.getDayOfMonth() + 1;
        if (remainingDays <= 0) {
            remainingDays = 1;
        }
        return available / remainingDays;
    }
}
