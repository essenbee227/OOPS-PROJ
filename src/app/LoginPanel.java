package app;

import managers.UserManager;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);

    public interface LoginListener {
        void onLoginSuccessful(String username);
        void onSignupRequested();
    }

    public LoginPanel(UserManager userManager, LoginListener listener) {
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 242, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Finance Tracker - Login");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setForeground(new Color(52, 152, 219));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        card.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        card.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        card.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        card.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(52, 152, 219));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton signupButton = new JButton("Go to Signup");
        signupButton.setBackground(new Color(149, 165, 166));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        signupButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(15, 5, 5, 5);
        card.add(loginButton, gbc);

        gbc.gridx = 1;
        card.add(signupButton, gbc);

        setLayout(new GridBagLayout());
        GridBagConstraints outer = new GridBagConstraints();
        outer.gridx = 0;
        outer.gridy = 0;
        outer.insets = new Insets(20, 20, 20, 20);
        add(card, outer);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            try {
                if (userManager.login(username, password)) {
                    listener.onLoginSuccessful(username);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        signupButton.addActionListener(e -> listener.onSignupRequested());
    }
}
