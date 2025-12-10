package com.example.ver4.controllers;

import com.example.ver4.Main;
import com.example.ver4.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class EditProfileController {
    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField addressField;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    private User currentUser;

    @FXML
    public void initialize() {
        System.out.println("=== EDIT PROFILE CONTROLLER INITIALIZE ===");

        currentUser = Main.getSystemController().getAuthManager().getCurrentUser();

        if (currentUser == null) {
            showMessage("Error: No user logged in!", true);
            return;
        }

        // Pre-fill fields with current data
        nameField.setText(currentUser.getName());
        emailField.setText(currentUser.getEmail());
        phoneField.setText(currentUser.getPhoneNumber());
        addressField.setText(currentUser.getAddress());

        System.out.println("Loaded profile for: " + currentUser.getUsername());
    }

    @FXML
    private void handleSave() {
        System.out.println("=== SAVE BUTTON CLICKED ===");

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate required fields
        if (name.isEmpty() || email.isEmpty()) {
            showMessage("Name and Email are required!", true);
            return;
        }

        if (!email.contains("@")) {
            showMessage("Invalid email format!", true);
            return;
        }

        // Update basic info
        currentUser.setName(name);
        currentUser.setEmail(email);
        currentUser.setPhoneNumber(phone);
        currentUser.setAddress(address);

        // Handle password change if requested
        if (!newPassword.isEmpty()) {
            // Validate current password
            if (!currentUser.getPassword().equals(currentPassword)) {
                showMessage("Current password is incorrect!", true);
                return;
            }

            // Validate new password
            if (newPassword.length() < 6) {
                showMessage("New password must be at least 6 characters!", true);
                return;
            }

            // Validate password confirmation
            if (!newPassword.equals(confirmPassword)) {
                showMessage("New passwords do not match!", true);
                return;
            }

            // Update password
            currentUser.setPassword(newPassword);
            System.out.println("Password updated successfully");
        }

        // Save to file
        try {
            Main.getSystemController().saveAll();
            showMessage("Profile updated successfully!", false);

            System.out.println("Profile saved for: " + currentUser.getUsername());

            // Wait 1 second then return to feed
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(() -> {
                        try {
                            Main.showFeedScreen();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            showMessage("Error saving profile: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        System.out.println("=== CANCEL BUTTON CLICKED ===");
        try {
            Main.showFeedScreen();
        } catch (Exception e) {
            showMessage("Cannot return to feed: " + e.getMessage(), true);
        }
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setTextFill(isError ? Color.RED : Color.GREEN);
        messageLabel.setVisible(true);
    }
}