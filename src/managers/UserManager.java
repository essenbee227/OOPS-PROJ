package managers;

import model.User;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private final List<User> users = new ArrayList<>();

    public boolean signup(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username and password must not be empty");
        }
        if (findByUsername(username) != null) {
            return false; // already exists
        }
        users.add(new User(username, password));
        return true;
    }

    public boolean login(String username, String password) {
        User user = findByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    public User getUser(String username) {
        return findByUsername(username);
    }

    public void setSavingsPercentage(String username, double percentage) {
        User user = findByUsername(username);
        if (user != null) {
            user.setSavingsPercentage(percentage);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public double getSavingsPercentage(String username) {
        User user = findByUsername(username);
        return user != null ? user.getSavingsPercentage() : 0.0;
    }

    private User findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}
