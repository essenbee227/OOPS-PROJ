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
        setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Finance Tracker - Login");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
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
        JButton signupButton = new JButton("Go to Signup");

        gbc.gridx = 0;
        gbc.gridy++;
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
