package com.example.ver3.controllers;

import com.example.ver3.Main;
import com.example.ver3.models.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CreatePostController {
    @FXML
    private TextArea contentArea;

    @FXML
    private Label charCountLabel;

    @FXML
    private RadioButton textPostRadio;

    @FXML
    private RadioButton statusPostRadio;

    @FXML
    private Label moodLabel;

    @FXML
    private ComboBox<String> moodComboBox;

    @FXML
    public void initialize() {
        System.out.println("=== CREATE POST CONTROLLER INITIALIZE ===");

        // Check if components are injected
        System.out.println("contentArea is null? " + (contentArea == null));
        System.out.println("textPostRadio is null? " + (textPostRadio == null));
        System.out.println("statusPostRadio is null? " + (statusPostRadio == null));
        System.out.println("moodComboBox is null? " + (moodComboBox == null));

        // Setup toggle group
        ToggleGroup group = new ToggleGroup();
        textPostRadio.setToggleGroup(group);
        statusPostRadio.setToggleGroup(group);

        // Select text post by default
        textPostRadio.setSelected(true);

        // Setup mood combo box
        moodComboBox.setItems(FXCollections.observableArrayList(
                "Happy", "Sad", "Excited", "Angry", "Neutral"
        ));
        moodComboBox.setValue("Happy");

        // Hide mood controls by default (since text post is selected)
        moodLabel.setVisible(false);
        moodComboBox.setVisible(false);

        // Add listener to content area for character count
        contentArea.textProperty().addListener((obs, oldText, newText) -> {
            charCountLabel.setText(newText.length() + " characters");
        });

        // Add listener to radio buttons
        textPostRadio.setOnAction(e -> {
            System.out.println("Text Post selected");
            moodLabel.setVisible(false);
            moodComboBox.setVisible(false);
        });

        statusPostRadio.setOnAction(e -> {
            System.out.println("Status Post selected");
            moodLabel.setVisible(true);
            moodComboBox.setVisible(true);
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

        // Create post based on type
        try {
            if (textPostRadio.isSelected()) {
                System.out.println("Creating TextPost...");
                Main.getSystemController()
                        .getPostManager()
                        .createPost(content, currentUser.getUsername());
            } else {
                String mood = moodComboBox.getValue();
                System.out.println("Creating StatusPost with mood: " + mood);
                Main.getSystemController()
                        .getPostManager()
                        .createPost(content, currentUser.getUsername(), mood);
            }

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