package app;

import managers.CalendarManager;
import managers.FinanceTracker;
import model.Event;
import model.Expense;
import services.BudgetAdjuster;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

public class DashboardPanel extends JPanel {

    private final FinanceTracker financeTracker;
    private final CalendarManager calendarManager;
    private final BudgetAdjuster budgetAdjuster;

    private final CardLayout innerCardLayout = new CardLayout();
    private final JPanel innerCards = new JPanel(innerCardLayout);

    private final JLabel incomeLabel = new JLabel();
    private final JLabel expenseLabel = new JLabel();
    private final JLabel balanceLabel = new JLabel();
    private final JLabel dailyLimitLabel = new JLabel();

    private final JTextArea analyticsEventsArea = new JTextArea(8, 30);
    private final JLabel analyticsSummaryLabel = new JLabel();

    private final DefaultTableModel calendarModel = new DefaultTableModel(6, 7) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable calendarTable = new JTable(calendarModel);
    private final PieChartPanel pieChartPanel = new PieChartPanel();
    private final JPanel pieLegendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private final JTextField amountField = new JTextField(8);
    private final JTextField typeField = new JTextField(10);
    private final JTextField descriptionField = new JTextField(12);

    private final JTextField eventTitleField = new JTextField(10);
    private final JTextField eventDateField = new JTextField(10); // yyyy-mm-dd
    private final JTextField eventCostField = new JTextField(8);
    private final JComboBox<String> eventCategoryCombo = new JComboBox<>(
            new String[]{"Birthday", "Outing", "Bills", "Groceries", "Travel", "Other"}
    );

    public DashboardPanel(FinanceTracker financeTracker,
                          CalendarManager calendarManager,
                          BudgetAdjuster budgetAdjuster,
                          String username) {
        this.financeTracker = financeTracker;
        this.calendarManager = calendarManager;
        this.budgetAdjuster = budgetAdjuster;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        JLabel welcome = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 18f));
        welcome.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(welcome, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        center.setBackground(new Color(245, 247, 250));

        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navBar.setOpaque(false);
        JButton transactionsPageButton = new JButton("Transactions Page");
        JButton eventsPageButton = new JButton("Events Page");
        JButton analyticsPageButton = new JButton("Analytics Page");

        Color accent = new Color(52, 152, 219);
        Color accentText = Color.WHITE;
        for (JButton btn : new JButton[]{transactionsPageButton, eventsPageButton, analyticsPageButton}) {
            btn.setBackground(accent);
            btn.setForeground(accentText);
            btn.setFocusPainted(false);
        }

        navBar.add(transactionsPageButton);
        navBar.add(eventsPageButton);
        navBar.add(analyticsPageButton);

        center.add(navBar, BorderLayout.NORTH);

        innerCards.setBackground(new Color(245, 247, 250));
        innerCards.add(createExpensePanel(), "transactions");
        innerCards.add(createEventPanel(), "events");
        innerCards.add(createAnalyticsPanel(), "analytics");

        center.add(innerCards, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
        add(createSummaryPanel(), BorderLayout.SOUTH);

        innerCardLayout.show(innerCards, "transactions");

        transactionsPageButton.addActionListener(e -> innerCardLayout.show(innerCards, "transactions"));
        eventsPageButton.addActionListener(e -> innerCardLayout.show(innerCards, "events"));
        analyticsPageButton.addActionListener(e -> {
            refreshAnalytics();
            innerCardLayout.show(innerCards, "analytics");
        });

        updateSummary();
    }

    private JPanel createExpensePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Amount (+ income, - expense):"), gbc);

        gbc.gridx = 1;
        panel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Type:"), gbc);

        gbc.gridx = 1;
        panel.add(typeField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        panel.add(descriptionField, gbc);

        JButton addButton = new JButton("Add Transaction");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                String type = typeField.getText().trim();
                String desc = descriptionField.getText().trim();

                Expense expense = new Expense(amount, LocalDate.now(), type, desc);
                financeTracker.addExpense(expense);

                amountField.setText("");
                typeField.setText("");
                descriptionField.setText("");

                updateSummary();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel title = new JLabel("Upcoming Events & Analytics");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(title, BorderLayout.NORTH);

        String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < headers.length; i++) {
            calendarTable.getColumnModel().getColumn(i).setHeaderValue(headers[i]);
        }
        calendarTable.getTableHeader().repaint();
        calendarTable.setRowHeight(24);

        JScrollPane calendarScroll = new JScrollPane(calendarTable);

        calendarTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = calendarTable.getSelectedRow();
                int col = calendarTable.getSelectedColumn();
                if (row < 0 || col < 0) {
                    return;
                }
                Object value = calendarModel.getValueAt(row, col);
                if (value == null) {
                    return;
                }
                String text = value.toString().trim();
                if (!text.endsWith("*")) {
                    return;
                }
                text = text.replace("*", "").trim();
                try {
                    int day = Integer.parseInt(text);
                    YearMonth currentMonth = YearMonth.now();
                    LocalDate date = currentMonth.atDay(day);

                    java.util.List<Event> events = calendarManager.getEvents();
                    StringBuilder msg = new StringBuilder("Events on " + date + ":\n\n");
                    for (Event ev : events) {
                        if (ev.getDate().equals(date)) {
                            msg.append("- ")
                               .append(ev.getTitle())
                               .append(" (")
                               .append(ev.getCategory())
                               .append(") : ")
                               .append(String.format("%.2f", ev.getExpectedCost()))
                               .append("\n");
                        }
                    }

                    if (msg.toString().endsWith(":\n\n")) {
                        msg.append("No events stored for this day.");
                    }

                    JOptionPane.showMessageDialog(DashboardPanel.this, msg.toString(), "Day Events", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ignored) {
                    // ignore clicks on non-day cells
                }
            }
        });

        pieChartPanel.setPreferredSize(new Dimension(250, 180));

        JPanel center = new JPanel(new GridLayout(1, 2, 10, 0));
        center.setOpaque(false);
        center.add(calendarScroll);

        JPanel right = new JPanel(new BorderLayout(0, 5));
        right.setOpaque(false);
        right.add(pieChartPanel, BorderLayout.CENTER);

        pieLegendPanel.setOpaque(false);
        right.add(pieLegendPanel, BorderLayout.SOUTH);

        center.add(right);

        panel.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(analyticsSummaryLabel, BorderLayout.WEST);
        panel.add(bottom, BorderLayout.SOUTH);

        refreshAnalytics();

        return panel;
    }

    private void refreshAnalytics() {
        StringBuilder builder = new StringBuilder();
        java.util.List<Event> events = calendarManager.getEvents();

        if (events.isEmpty()) {
            builder.append("No upcoming events. Add some on the Events page.\n");
            analyticsEventsArea.setText(builder.toString());
            analyticsSummaryLabel.setText("");

            for (int r = 0; r < calendarModel.getRowCount(); r++) {
                for (int c = 0; c < calendarModel.getColumnCount(); c++) {
                    calendarModel.setValueAt("", r, c);
                }
            }
            pieChartPanel.setCategoryTotals(new HashMap<>());
            return;
        }

        events.stream()
                .sorted(java.util.Comparator.comparing(Event::getDate))
                .forEach(event -> builder.append(event.getDate())
                        .append("  -  ")
                        .append(event.getTitle())
                        .append("  (")
                        .append(event.getCategory())
                        .append(")  :  ")
                        .append(String.format("%.2f", event.getExpectedCost()))
                        .append("\n"));

        analyticsEventsArea.setText(builder.toString());

        YearMonth currentMonth = YearMonth.now();
        LocalDate start = currentMonth.atDay(1);
        LocalDate end = currentMonth.atEndOfMonth();

        for (int r = 0; r < calendarModel.getRowCount(); r++) {
            for (int c = 0; c < calendarModel.getColumnCount(); c++) {
                calendarModel.setValueAt("", r, c);
            }
        }

        java.util.Set<Integer> daysWithEvents = new java.util.HashSet<>();
        for (Event e : events) {
            if (!e.getDate().isBefore(start) && !e.getDate().isAfter(end)) {
                daysWithEvents.add(e.getDate().getDayOfMonth());
            }
        }

        int lengthOfMonth = currentMonth.lengthOfMonth();
        int startColumn = start.getDayOfWeek().getValue() % 7;
        int day = 1;
        for (int r = 0; r < calendarModel.getRowCount() && day <= lengthOfMonth; r++) {
            for (int c = 0; c < calendarModel.getColumnCount() && day <= lengthOfMonth; c++) {
                if (r == 0 && c < startColumn) {
                    calendarModel.setValueAt("", r, c);
                } else {
                    String text = String.valueOf(day);
                    if (daysWithEvents.contains(day)) {
                        text += " *";
                    }
                    calendarModel.setValueAt(text, r, c);
                    day++;
                }
            }
        }

        double totalThisMonth = calendarManager.getTotalExpectedCostBetween(start, end);

        Map<String, Double> categoryTotals = new HashMap<>();
        for (Event e : events) {
            if (!e.getDate().isBefore(start) && !e.getDate().isAfter(end)) {
                categoryTotals.merge(e.getCategory(), e.getExpectedCost(), Double::sum);
            }
        }
        pieChartPanel.setCategoryTotals(categoryTotals);

        updatePieLegend(categoryTotals);

        analyticsSummaryLabel.setText("Total expected cost this month: " + String.format("%.2f", totalThisMonth));
    }

    private void updatePieLegend(Map<String, Double> categoryTotals) {
        pieLegendPanel.removeAll();
        if (categoryTotals == null || categoryTotals.isEmpty()) {
            pieLegendPanel.revalidate();
            pieLegendPanel.repaint();
            return;
        }

        Color[] colors = {
                new Color(52, 152, 219),
                new Color(46, 204, 113),
                new Color(231, 76, 60),
                new Color(155, 89, 182),
                new Color(241, 196, 15),
                new Color(26, 188, 156)
        };

        int index = 0;
        for (String category : categoryTotals.keySet()) {
            Color c = colors[index % colors.length];
            JPanel swatch = new JPanel();
            swatch.setBackground(c);
            swatch.setPreferredSize(new Dimension(14, 14));

            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            item.setOpaque(false);
            item.add(swatch);
            item.add(new JLabel(category));

            pieLegendPanel.add(item);
            index++;
        }

        pieLegendPanel.revalidate();
        pieLegendPanel.repaint();
    }

    private static class PieChartPanel extends JPanel {
        private Map<String, Double> categoryTotals = new HashMap<>();

        public void setCategoryTotals(Map<String, Double> categoryTotals) {
            this.categoryTotals = categoryTotals;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (categoryTotals == null || categoryTotals.isEmpty()) {
                return;
            }

            double total = 0.0;
            for (double v : categoryTotals.values()) {
                total += v;
            }
            if (total <= 0) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 20;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            Color[] colors = {
                    new Color(52, 152, 219),
                    new Color(46, 204, 113),
                    new Color(231, 76, 60),
                    new Color(155, 89, 182),
                    new Color(241, 196, 15),
                    new Color(26, 188, 156)
            };

            int colorIndex = 0;
            int startAngle = 0;
            for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                double value = entry.getValue();
                int angle = (int) Math.round(value * 360 / total);
                g2.setColor(colors[colorIndex % colors.length]);
                g2.fillArc(x, y, size, size, startAngle, angle);
                startAngle += angle;
                colorIndex++;
            }

            g2.dispose();
        }
    }

    private JPanel createEventPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        panel.add(eventTitleField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Date (yyyy-mm-dd):"), gbc);

        gbc.gridx = 1;
        panel.add(eventDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Expected Cost:"), gbc);

        gbc.gridx = 1;
        panel.add(eventCostField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        panel.add(eventCategoryCombo, gbc);

        JButton addEventButton = new JButton("Add Event");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(addEventButton, gbc);

        addEventButton.addActionListener(e -> {
            try {
                String title = eventTitleField.getText().trim();
                LocalDate date = LocalDate.parse(eventDateField.getText().trim());
                double cost = Double.parseDouble(eventCostField.getText().trim());
                String category = (String) eventCategoryCombo.getSelectedItem();

                Event event = new Event(title, date, cost, category);
                calendarManager.addEvent(event);

                eventTitleField.setText("");
                eventDateField.setText("");
                eventCostField.setText("");
                eventCategoryCombo.setSelectedIndex(0);

                updateSummary();
                refreshAnalytics();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for cost.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Please enter date as yyyy-mm-dd.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.setBorder(BorderFactory.createTitledBorder("Summary"));

        panel.add(incomeLabel);
        panel.add(expenseLabel);
        panel.add(balanceLabel);
        panel.add(dailyLimitLabel);

        return panel;
    }

    private void updateSummary() {
        double income = financeTracker.getTotalIncome();
        double expenses = financeTracker.getTotalExpenses();
        double balance = financeTracker.getBalance();

        double dailyLimit = budgetAdjuster.calculateDailyLimit(
                financeTracker,
                calendarManager,
                LocalDate.now(),
                YearMonth.now()
        );

        incomeLabel.setText("Total Income: " + String.format("%.2f", income));
        expenseLabel.setText("Total Expenses: " + String.format("%.2f", expenses));
        balanceLabel.setText("Balance: " + String.format("%.2f", balance));
        dailyLimitLabel.setText("Suggested Daily Limit: " + String.format("%.2f", dailyLimit));
    }
}
