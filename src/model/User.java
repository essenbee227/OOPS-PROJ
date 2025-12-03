package model;

public class User {
    private String username;
    private String password;
    private double savingsPercentage = 0.0; // Default to 0%

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getSavingsPercentage() {
        return savingsPercentage;
    }

    public void setSavingsPercentage(double savingsPercentage) {
        if (savingsPercentage < 0 || savingsPercentage > 100) {
            throw new IllegalArgumentException("Savings percentage must be between 0 and 100");
        }
        this.savingsPercentage = savingsPercentage;
    }
}
