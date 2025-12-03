package services;

import managers.CalendarManager;
import managers.FinanceTracker;

import java.time.LocalDate;
import java.time.YearMonth;

public class BudgetAdjuster {

    public double calculateDailyLimit(FinanceTracker financeTracker,
                                      CalendarManager calendarManager,
                                      LocalDate today,
                                      YearMonth month,
                                      double savingsPercentage) {
        double totalIncome = financeTracker.getTotalIncome();
        
        // Calculate savings amount based on percentage
        double savingsAmount = (totalIncome * savingsPercentage) / 100.0;
        
        // Calculate available balance after savings
        double balance = financeTracker.getBalance();
        double availableAfterSavings = balance - savingsAmount;
        
        LocalDate start = today;
        LocalDate end = month.atEndOfMonth();
        double upcomingCosts = calendarManager.getTotalExpectedCostBetween(start, end);

        // Available for spending = balance - savings - upcoming costs
        double available = availableAfterSavings - upcomingCosts;
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
