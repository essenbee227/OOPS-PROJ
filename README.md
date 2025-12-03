# Finance & Calendar Tracker

A Java Swing-based desktop application for managing personal finances, tracking expenses, scheduling events, and calculating daily spending budgets based on savings goals.

## Features

- **User Authentication**: Secure login and signup system
- **Transaction Management**: Add income and expenses with categorization
- **Event Scheduling**: Schedule future events with expected costs
- **Savings Goals**: Set savings percentage of income to track savings targets
- **Daily Budget Calculator**: Automatically calculates daily spending budget based on:
  - Total income
  - Savings percentage
  - Upcoming event costs
  - Remaining days in the month
- **Multi-Month Calendar View**: View events across 6 months in an interactive calendar
- **Analytics Dashboard**: 
  - Visual pie chart showing expense categories
  - Calendar view with event markers
  - Financial summary with income, expenses, balance, and daily budget
- **Modern UI**: Clean, intuitive interface with improved styling

## System Architecture

### Architecture Overview

The application follows a **layered architecture** pattern with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                      Presentation Layer                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐     │
│  │MainApp   │  │Dashboard │  │LoginPanel│  │SignupPanel│    │
│  │(JFrame)  │  │Panel     │  │          │  │          │     │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘     │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      Business Logic Layer                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │FinanceTracker│  │CalendarManager│ │UserManager   │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                        Service Layer                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              BudgetAdjuster                          │   │
│  │  (Calculates daily budget with savings consideration)│   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                         Data Layer                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                   │
│  │  User    │  │  Event   │  │ Expense  │                   │
│  └──────────┘  └──────────┘  └──────────┘                   │
└─────────────────────────────────────────────────────────────┘
```

### Component Details

#### 1. Presentation Layer (`app` package)

**MainApp.java**
- Entry point of the application
- Manages the main window (JFrame) and CardLayout for screen navigation
- Initializes all managers and services
- Coordinates between login, signup, and dashboard panels

**LoginPanel.java**
- Handles user authentication
- Validates credentials through UserManager
- Navigates to dashboard on successful login

**SignupPanel.java**
- Creates new user accounts
- Validates input and checks for duplicate usernames
- Returns to login after successful signup

**DashboardPanel.java**
- Main application interface with multiple views:
  - **Transactions Page**: Add income/expenses
  - **Events Page**: Schedule future events
  - **Analytics Page**: Multi-month calendar and pie charts
  - **Settings Page**: Configure savings percentage
- Displays financial summary (income, expenses, balance, daily budget)
- Updates UI in real-time as data changes

#### 2. Business Logic Layer (`managers` package)

**UserManager.java**
- Manages user accounts (signup, login, authentication)
- Stores user preferences (savings percentage)
- Maintains in-memory user database

**FinanceTracker.java**
- Manages all financial transactions (income and expenses)
- Calculates:
  - Total income (positive amounts)
  - Total expenses (negative amounts)
  - Current balance
- Provides immutable access to expense list

**CalendarManager.java**
- Manages scheduled events
- Filters events by date ranges
- Calculates total expected costs for time periods
- Provides event querying capabilities

#### 3. Service Layer (`services` package)

**BudgetAdjuster.java**
- Calculates daily spending budget
- Formula: `Daily Budget = (Balance - Savings - Upcoming Costs) / Remaining Days`
- Considers:
  - User's savings percentage goal
  - Upcoming event costs
  - Remaining days in the month

#### 4. Data Layer (`model` package)

**User.java**
- Represents a user account
- Stores: username, password, savings percentage

**Expense.java**
- Represents a financial transaction
- Stores: amount (positive for income, negative for expenses), date, type, description

**Event.java**
- Represents a scheduled event
- Stores: title, date, expected cost, category

### Data Flow

```
User Input (UI)
    │
    ▼
Presentation Layer (Panels)
    │
    ▼
Business Logic Layer (Managers)
    │
    ▼
Service Layer (BudgetAdjuster)
    │
    ▼
Data Models (User, Expense, Event)
    │
    ▼
In-Memory Storage (Lists)
```

### Key Design Patterns

1. **MVC-like Pattern**: Separation of UI (View), Business Logic (Model/Controller), and Data
2. **Manager Pattern**: Centralized management of related entities
3. **Service Pattern**: Reusable business logic services
4. **Observer Pattern**: UI updates when data changes
5. **CardLayout Pattern**: Screen navigation management

## Project Structure

```
OOPS-PROJ/
└── src/
    ├── app/                    # Presentation Layer
    │   ├── MainApp.java        # Main application entry point
    │   ├── DashboardPanel.java # Main dashboard interface
    │   ├── LoginPanel.java     # Login screen
    │   └── SignupPanel.java    # Signup screen
    │
    ├── managers/               # Business Logic Layer
    │   ├── UserManager.java    # User account management
    │   ├── FinanceTracker.java # Financial transaction management
    │   └── CalendarManager.java # Event scheduling management
    │
    ├── model/                  # Data Layer
    │   ├── User.java           # User data model
    │   ├── Expense.java        # Transaction data model
    │   └── Event.java          # Event data model
    │
    └── services/               # Service Layer
        └── BudgetAdjuster.java # Budget calculation service
```

## Technologies Used

- **Java**: Core programming language
- **Java Swing**: GUI framework for desktop application
- **Java AWT**: Additional UI components and layout managers
- **Java 8+ Time API**: Date and time handling (LocalDate, YearMonth)

## Requirements

- Java JDK 8 or higher
- Any Java-compatible IDE (IntelliJ IDEA, Eclipse, VS Code) or command line

## How to Run

### Using Command Line

1. Navigate to the project directory:
   ```bash
   cd OOPS-PROJ
   ```

2. Compile the Java files:
   ```bash
   javac -d . -sourcepath src src/app/MainApp.java
   ```

3. Run the application:
   ```bash
   java app.MainApp
   ```

### Using an IDE

1. Open the project in your IDE (IntelliJ IDEA, Eclipse, etc.)
2. Ensure the `src` directory is marked as the source root
3. Run the `MainApp.java` file (it contains the `main` method)

## Usage Instructions

### Getting Started

1. **Sign Up**: Create a new account with a username and password
2. **Login**: Use your credentials to access the dashboard

### Managing Finances

1. **Add Income**: 
   - Go to Transactions Page
   - Enter a positive amount (e.g., `5000`)
   - Add type and description
   - Click "Add Transaction"

2. **Add Expenses**:
   - Go to Transactions Page
   - Enter a negative amount (e.g., `-100`)
   - Add type and description
   - Click "Add Transaction"

### Setting Savings Goals

1. Go to **Settings Page**
2. Enter your desired savings percentage (0-100%)
3. Click "Save Settings"
4. Your daily budget will automatically adjust

### Scheduling Events

1. Go to **Events Page**
2. Enter event details:
   - Title
   - Date (format: yyyy-mm-dd)
   - Expected Cost
   - Category
3. Click "Add Event"
4. View events in the **Analytics Page** calendar

### Viewing Analytics

1. Go to **Analytics Page**
2. View:
   - **Multi-month calendar**: See events across 6 months
   - **Pie chart**: Visual breakdown of event categories
   - **Financial summary**: Income, expenses, balance, and daily budget

### Daily Budget Calculation

The daily budget is calculated as:
```
Daily Budget = (Current Balance - Savings Amount - Upcoming Event Costs) / Remaining Days in Month
```

Where:
- **Savings Amount** = (Total Income × Savings Percentage) / 100
- **Upcoming Event Costs** = Sum of all event costs for remaining days in the month

## Features in Detail

### Savings Goal Tracking
- Set a percentage of income to save (e.g., 20%)
- System automatically calculates savings amount
- Daily budget adjusts to ensure savings goal is met

### Multi-Month Calendar
- Displays current month + next 5 months
- Events marked with asterisk (*)
- Click on marked days to see event details
- Scrollable interface for easy navigation

### Financial Summary
- **Total Income**: Sum of all positive transactions
- **Total Expenses**: Sum of all negative transactions (displayed as positive)
- **Balance**: Net amount (income - expenses)
- **Daily Budget**: Recommended daily spending limit

## Future Enhancements

Potential improvements for future versions:
- Data persistence (file/database storage)
- Export reports (PDF/CSV)
- Budget alerts and notifications
- Recurring transactions/events
- Multiple currency support
- Data visualization charts
- Transaction history search/filter

## License

This project is created for educational purposes.

## Author

Developed as part of an Object-Oriented Programming Systems (OOPS) project.

