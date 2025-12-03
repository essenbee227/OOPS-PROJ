package app;

import managers.CalendarManager;
import managers.FinanceTracker;
import managers.UserManager;
import services.BudgetAdjuster;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private final UserManager userManager = new UserManager();
    private final FinanceTracker financeTracker = new FinanceTracker();
    private final CalendarManager calendarManager = new CalendarManager();
    private final BudgetAdjuster budgetAdjuster = new BudgetAdjuster();

    public MainApp() {
        setTitle("Finance & Calendar Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));

        getContentPane().setBackground(new Color(240, 242, 245));

        createCards();
        setContentPane(cards);
    }

    private void createCards() {
        LoginPanel loginPanel = new LoginPanel(userManager, new LoginPanel.LoginListener() {
            @Override
            public void onLoginSuccessful(String username) {
                showDashboard(username);
            }

            @Override
            public void onSignupRequested() {
                cardLayout.show(cards, "signup");
            }
        });

        SignupPanel signupPanel = new SignupPanel(userManager, new SignupPanel.SignupListener() {
            @Override
            public void onSignupSuccessful() {
                cardLayout.show(cards, "login");
            }

            @Override
            public void onBackToLogin() {
                cardLayout.show(cards, "login");
            }
        });

        cards.add(loginPanel, "login");
        cards.add(signupPanel, "signup");

        cardLayout.show(cards, "login");
    }

    private void showDashboard(String username) {
        DashboardPanel dashboard = new DashboardPanel(financeTracker, calendarManager, budgetAdjuster, userManager, username);
        cards.add(dashboard, "dashboard");
        cardLayout.show(cards, "dashboard");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {
                // Fallback to default look and feel
            }

            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }
}
