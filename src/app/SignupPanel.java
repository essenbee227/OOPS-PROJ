package app;

import managers.UserManager;

import javax.swing.*;
import java.awt.*;

public class SignupPanel extends JPanel {

    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);

    public interface SignupListener {
        void onSignupSuccessful();
        void onBackToLogin();
    }

    public SignupPanel(UserManager userManager, SignupListener listener) {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Create Account");
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

        JButton signupButton = new JButton("Sign Up");
        JButton backButton = new JButton("Back to Login");

        gbc.gridx = 1;
        gbc.gridy++;
        card.add(backButton, gbc);

        setLayout(new GridBagLayout());
        GridBagConstraints outer = new GridBagConstraints();
        outer.gridx = 0;
        outer.gridy = 0;
        outer.insets = new Insets(20, 20, 20, 20);
        add(card, outer);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTH;
        add(signupButton, gbc);

        signupButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            try {
                boolean created = userManager.signup(username, password);
                if (created) {
                    JOptionPane.showMessageDialog(this, "Account created. You can log in now.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    listener.onSignupSuccessful();
                } else {
                    JOptionPane.showMessageDialog(this, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> listener.onBackToLogin());
    }
}
