package com.example.ver4.controllers;

import com.example.ver4.Main;
import com.example.ver4.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CreatePostController {
    @FXML
    private TextArea contentArea;

    @FXML
    private Label charCountLabel;

    @FXML
    public void initialize() {
        System.out.println("=== CREATE POST CONTROLLER INITIALIZE ===");

        // Add listener to content area for character count
        contentArea.textProperty().addListener((obs, oldText, newText) -> {
            int length = newText.length();
            charCountLabel.setText(length + " / 500 characters");

            // Change color if approaching limit
            if (length > 450) {
                charCountLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            } else if (length > 400) {
                charCountLabel.setStyle("-fx-text-fill: #f39c12;");
            } else {
                charCountLabel.setStyle("-fx-text-fill: #7f8c8d;");
            }
        });

        System.out.println("Create Post Controller initialized successfully");
    }

    @FXML
    private void handlePost() {
        System.out.println("=== POST BUTTON CLICKED ===");

        String content = contentArea.getText().trim();

        System.out.println("Content length: " + content.length());

        // Validate content
        if (content.isEmpty()) {
            System.out.println("Content is empty");
            showAlert("Error", "Content cannot be empty!");
            return;
        }

        if (content.length() > 500) {
            System.out.println("Content too long");
            showAlert("Error", "Content too long! Maximum 500 characters.");
            return;
        }

        // Get current user
        User currentUser = Main.getSystemController()
                .getAuthManager()
                .getCurrentUser();

        if (currentUser == null) {
            System.out.println("No user logged in");
            showAlert("Error", "You must be logged in to post!");
            return;
        }

        System.out.println("Current user: " + currentUser.getUsername());

        // Create text post (single type)
        try {
            System.out.println("Creating TextPost...");
            Main.getSystemController()
                    .getPostManager()
                    .createPost(content, currentUser.getUsername());

            // Save data
            System.out.println("Saving data...");
            Main.getSystemController().saveAll();

            showAlert("Success", "Post created successfully!");

            System.out.println("Navigating back to feed...");

            // Go back to feed
            Main.showFeedScreen();

        } catch (Exception e) {
            System.out.println("Error creating post: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to create post: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        System.out.println("=== CANCEL BUTTON CLICKED ===");
        try {
            Main.showFeedScreen();
        } catch (Exception e) {
            System.out.println("Error returning to feed: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Cannot return to feed: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}