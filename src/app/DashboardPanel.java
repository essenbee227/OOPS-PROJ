package app;

import managers.CalendarManager;
import managers.FinanceTracker;
import managers.UserManager;
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
    private final UserManager userManager;
    private final String username;

    private final CardLayout innerCardLayout = new CardLayout();
    private final JPanel innerCards = new JPanel(innerCardLayout);

    private final JLabel incomeLabel = new JLabel();
    private final JLabel expenseLabel = new JLabel();
    private final JLabel balanceLabel = new JLabel();
    private final JLabel dailyLimitLabel = new JLabel();

    private final JTextArea analyticsEventsArea = new JTextArea(8, 30);
    private final JLabel analyticsSummaryLabel = new JLabel();
    private final PieChartPanel pieChartPanel = new PieChartPanel();
    private final JPanel pieLegendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JPanel multiMonthCalendarPanel = new JPanel();

    private final JTextField amountField = new JTextField(8);
    private final JTextField typeField = new JTextField(10);
    private final JTextField descriptionField = new JTextField(12);

    private final JTextField eventTitleField = new JTextField(10);
    private final JTextField eventDateField = new JTextField(10); // yyyy-mm-dd
    private final JTextField eventCostField = new JTextField(8);
    private final JComboBox<String> eventCategoryCombo = new JComboBox<>(
            new String[]{"Birthday", "Outing", "Bills", "Groceries", "Travel", "Other"}
    );
    
    private final JTextField savingsPercentageField = new JTextField(8);
    private final JLabel savingsLabel = new JLabel();

    public DashboardPanel(FinanceTracker financeTracker,
                          CalendarManager calendarManager,
                          BudgetAdjuster budgetAdjuster,
                          UserManager userManager,
                          String username) {
        this.financeTracker = financeTracker;
        this.calendarManager = calendarManager;
        this.budgetAdjuster = budgetAdjuster;
        this.userManager = userManager;
        this.username = username;

        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        // Enhanced welcome header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel welcome = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 22f));
        welcome.setForeground(Color.WHITE);
        headerPanel.add(welcome, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        center.setBackground(new Color(240, 242, 245));

        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        navBar.setOpaque(false);
        JButton transactionsPageButton = new JButton("Transactions");
        JButton eventsPageButton = new JButton("Events");
        JButton analyticsPageButton = new JButton("Analytics");
        JButton settingsPageButton = new JButton("Settings");

        Color accent = new Color(52, 152, 219);
        Color accentHover = new Color(41, 128, 185);
        Color accentText = Color.WHITE;
        Font buttonFont = new Font(Font.SANS_SERIF, Font.BOLD, 13);
        
        for (JButton btn : new JButton[]{transactionsPageButton, eventsPageButton, analyticsPageButton, settingsPageButton}) {
            btn.setBackground(accent);
            btn.setForeground(accentText);
            btn.setFocusPainted(false);
            btn.setFont(buttonFont);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentHover, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
            ));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        navBar.add(transactionsPageButton);
        navBar.add(eventsPageButton);
        navBar.add(analyticsPageButton);
        navBar.add(settingsPageButton);

        center.add(navBar, BorderLayout.NORTH);

        innerCards.setBackground(new Color(240, 242, 245));
        innerCards.add(createExpensePanel(), "transactions");
        innerCards.add(createEventPanel(), "events");
        innerCards.add(createAnalyticsPanel(), "analytics");
        innerCards.add(createSettingsPanel(), "settings");

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
        settingsPageButton.addActionListener(e -> {
            loadSavingsPercentage();
            innerCardLayout.show(innerCards, "settings");
        });

        updateSummary();
    }
    
    private void loadSavingsPercentage() {
        double currentPercentage = userManager.getSavingsPercentage(username);
        savingsPercentageField.setText(String.format("%.1f", currentPercentage));
    }

    private JPanel createExpensePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
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
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        addButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 4, 4, 4);
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
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("Upcoming Events & Analytics");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(title, BorderLayout.NORTH);

        multiMonthCalendarPanel.setLayout(new BoxLayout(multiMonthCalendarPanel, BoxLayout.Y_AXIS));
        multiMonthCalendarPanel.setBackground(Color.WHITE);
        
        JScrollPane calendarScroll = new JScrollPane(multiMonthCalendarPanel);
        calendarScroll.setPreferredSize(new Dimension(500, 400));
        calendarScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        calendarScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

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
            multiMonthCalendarPanel.removeAll();
            multiMonthCalendarPanel.revalidate();
            multiMonthCalendarPanel.repaint();
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

        // Clear existing calendar panels
        multiMonthCalendarPanel.removeAll();

        // Show 6 months: current month and next 5 months
        YearMonth startMonth = YearMonth.now();
        double totalCost = 0.0;
        Map<String, Double> categoryTotals = new HashMap<>();

        for (int i = 0; i < 6; i++) {
            YearMonth month = startMonth.plusMonths(i);
            JPanel monthPanel = createMonthCalendar(month, events);
            multiMonthCalendarPanel.add(monthPanel);
            multiMonthCalendarPanel.add(Box.createVerticalStrut(15));

            LocalDate monthStart = month.atDay(1);
            LocalDate monthEnd = month.atEndOfMonth();
            totalCost += calendarManager.getTotalExpectedCostBetween(monthStart, monthEnd);

            for (Event e : events) {
                if (!e.getDate().isBefore(monthStart) && !e.getDate().isAfter(monthEnd)) {
                    categoryTotals.merge(e.getCategory(), e.getExpectedCost(), Double::sum);
                }
            }
        }

        multiMonthCalendarPanel.revalidate();
        multiMonthCalendarPanel.repaint();

        pieChartPanel.setCategoryTotals(categoryTotals);
        updatePieLegend(categoryTotals);

        analyticsSummaryLabel.setText("Total expected cost (next 6 months): " + String.format("%.2f", totalCost));
    }

    private JPanel createMonthCalendar(YearMonth month, java.util.List<Event> events) {
        JPanel monthPanel = new JPanel(new BorderLayout());
        monthPanel.setBackground(Color.WHITE);
        monthPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Month header
        JLabel monthLabel = new JLabel(month.getMonth().toString() + " " + month.getYear(), SwingConstants.CENTER);
        monthLabel.setFont(monthLabel.getFont().deriveFont(Font.BOLD, 14f));
        monthLabel.setForeground(new Color(52, 152, 219));
        monthLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        monthPanel.add(monthLabel, BorderLayout.NORTH);

        // Calendar table
        DefaultTableModel monthModel = new DefaultTableModel(6, 7) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable monthTable = new JTable(monthModel);
        monthTable.setRowHeight(30);
        monthTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        monthTable.setGridColor(new Color(220, 220, 220));
        monthTable.setShowGrid(true);
        monthTable.setIntercellSpacing(new Dimension(2, 2));

        String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < headers.length; i++) {
            monthTable.getColumnModel().getColumn(i).setHeaderValue(headers[i]);
        }
        monthTable.getTableHeader().repaint();
        monthTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));

        // Populate calendar
        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();
        java.util.Set<LocalDate> daysWithEvents = new java.util.HashSet<>();
        
        for (Event e : events) {
            if (!e.getDate().isBefore(monthStart) && !e.getDate().isAfter(monthEnd)) {
                daysWithEvents.add(e.getDate());
            }
        }

        int lengthOfMonth = month.lengthOfMonth();
        int startColumn = monthStart.getDayOfWeek().getValue() % 7;
        int day = 1;
        
        for (int r = 0; r < monthModel.getRowCount() && day <= lengthOfMonth; r++) {
            for (int c = 0; c < monthModel.getColumnCount() && day <= lengthOfMonth; c++) {
                if (r == 0 && c < startColumn) {
                    monthModel.setValueAt("", r, c);
                } else {
                    LocalDate currentDate = month.atDay(day);
                    String text = String.valueOf(day);
                    if (daysWithEvents.contains(currentDate)) {
                        text += " *";
                    }
                    monthModel.setValueAt(text, r, c);
                    day++;
                }
            }
        }

        // Add click listener
        final YearMonth monthFinal = month;
        monthTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = monthTable.getSelectedRow();
                int col = monthTable.getSelectedColumn();
                if (row < 0 || col < 0) {
                    return;
                }
                Object value = monthModel.getValueAt(row, col);
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
                    LocalDate date = monthFinal.atDay(day);

                    java.util.List<Event> allEvents = calendarManager.getEvents();
                    StringBuilder msg = new StringBuilder("Events on " + date + ":\n\n");
                    for (Event ev : allEvents) {
                        if (ev.getDate().equals(date)) {
                            msg.append("- ")
                               .append(ev.getTitle())
                               .append(" (")
                               .append(ev.getCategory())
                               .append(") : $")
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

        JScrollPane tableScroll = new JScrollPane(monthTable);
        tableScroll.setBorder(null);
        tableScroll.setPreferredSize(new Dimension(400, 180));
        monthPanel.add(tableScroll, BorderLayout.CENTER);

        return monthPanel;
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
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
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
        addEventButton.setBackground(new Color(46, 204, 113));
        addEventButton.setForeground(Color.WHITE);
        addEventButton.setFocusPainted(false);
        addEventButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        addEventButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        addEventButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 4, 4, 4);
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
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(new Color(52, 152, 219));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                "Financial Summary",
                0, 0,
                new Font(Font.SANS_SERIF, Font.BOLD, 14),
                Color.WHITE
            )
        ));
        
        // Style the labels
        Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 13);
        Color labelColor = Color.WHITE;
        
        incomeLabel.setFont(labelFont);
        incomeLabel.setForeground(labelColor);
        expenseLabel.setFont(labelFont);
        expenseLabel.setForeground(labelColor);
        balanceLabel.setFont(labelFont);
        balanceLabel.setForeground(labelColor);
        dailyLimitLabel.setFont(labelFont);
        dailyLimitLabel.setForeground(labelColor);
        
        // Create styled panels for each metric
        JPanel incomePanel = createMetricPanel(incomeLabel);
        JPanel expensePanel = createMetricPanel(expenseLabel);
        JPanel balancePanel = createMetricPanel(balanceLabel);
        JPanel dailyLimitPanel = createMetricPanel(dailyLimitLabel);
        
        panel.add(incomePanel);
        panel.add(expensePanel);
        panel.add(balancePanel);
        panel.add(dailyLimitPanel);

        return panel;
    }
    
    private JPanel createMetricPanel(JLabel label) {
        JPanel metricPanel = new JPanel(new BorderLayout());
        metricPanel.setBackground(new Color(41, 128, 185));
        metricPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        metricPanel.add(label, BorderLayout.CENTER);
        return metricPanel;
    }
    
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel title = new JLabel("Savings Settings");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setForeground(new Color(52, 152, 219));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        JLabel savingsLabel = new JLabel("Savings Percentage (%):");
        savingsLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        panel.add(savingsLabel, gbc);
        
        gbc.gridx = 1;
        savingsPercentageField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        panel.add(savingsPercentageField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        this.savingsLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        this.savingsLabel.setForeground(new Color(100, 100, 100));
        panel.add(this.savingsLabel, gbc);
        
        JButton saveButton = new JButton("Save Settings");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        saveButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        gbc.gridy++;
        gbc.insets = new Insets(20, 4, 4, 4);
        panel.add(saveButton, gbc);
        
        saveButton.addActionListener(e -> {
            try {
                double percentage = Double.parseDouble(savingsPercentageField.getText().trim());
                if (percentage < 0 || percentage > 100) {
                    JOptionPane.showMessageDialog(this, 
                        "Savings percentage must be between 0 and 100.", 
                        "Input Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                userManager.setSavingsPercentage(username, percentage);
                JOptionPane.showMessageDialog(this, 
                    "Savings percentage updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                updateSummary();
                updateSavingsLabel();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid number for savings percentage.", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        updateSavingsLabel();
        return panel;
    }
    
    private void updateSavingsLabel() {
        double percentage = userManager.getSavingsPercentage(username);
        double income = financeTracker.getTotalIncome();
        double savingsAmount = (income * percentage) / 100.0;
        savingsLabel.setText(String.format("Current: %.1f%% (%.2f saved from total income of %.2f)", 
            percentage, savingsAmount, income));
    }

    private void updateSummary() {
        double income = financeTracker.getTotalIncome();
        double expenses = financeTracker.getTotalExpenses();
        double balance = financeTracker.getBalance();

        double savingsPercentage = userManager.getSavingsPercentage(username);
        double dailyLimit = budgetAdjuster.calculateDailyLimit(
                financeTracker,
                calendarManager,
                LocalDate.now(),
                YearMonth.now(),
                savingsPercentage
        );

        incomeLabel.setText("💰 Total Income: $" + String.format("%.2f", income));
        expenseLabel.setText("💸 Total Expenses: $" + String.format("%.2f", Math.abs(expenses)));
        balanceLabel.setText("💵 Balance: $" + String.format("%.2f", balance));
        dailyLimitLabel.setText("📊 Daily Budget: $" + String.format("%.2f", dailyLimit));
        
        updateSavingsLabel();
    }
}
