package com.example.ver3.controllers;

import com.example.ver3.Main;
import com.example.ver3.models.Comment;
import com.example.ver3.models.Post;
import com.example.ver3.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;

public class PostDetailController {
    @FXML
    private Label authorLabel;

    @FXML
    private Label timestampLabel;

    @FXML
    private Label postTypeLabel;

    @FXML
    private Label contentLabel;

    @FXML
    private Label likesLabel;

    @FXML
    private Label commentCountLabel;

    @FXML
    private Button likeButton;

    @FXML
    private ListView<String> commentsListView;

    @FXML
    private TextArea commentArea;

    private Post currentPost;

    @FXML
    public void initialize() {
        System.out.println("=== POST DETAIL CONTROLLER INITIALIZE ===");

        currentPost = FeedController.getSelectedPost();

        if (currentPost == null) {
            System.err.println("ERROR: No post selected!");
            showAlert("Error", "No post selected");
            return;
        }

        System.out.println("Loading post: " + currentPost.getPostId());
        loadPostDetails();
    }

    private void loadPostDetails() {
        System.out.println("=== LOADING POST DETAILS ===");

        // Display post information
        authorLabel.setText("Author: " + currentPost.getAuthorName());
        timestampLabel.setText("Posted: " + currentPost.getTimestamp());
        postTypeLabel.setText("Type: " + currentPost.getPostType());
        contentLabel.setText(currentPost.getContent());
        likesLabel.setText("Likes: " + currentPost.getLikeCount());

        // Load comments
        loadComments();

        System.out.println("Post details loaded successfully");
    }

    private void loadComments() {
        System.out.println("Loading comments...");
        ArrayList<Comment> comments = currentPost.getComments();
        System.out.println("Found " + comments.size() + " comments");

        ObservableList<String> commentStrings = FXCollections.observableArrayList();

        if (comments.isEmpty()) {
            commentStrings.add("No comments yet. Be the first to comment!");
        } else {
            for (Comment comment : comments) {
                String commentText = comment.getAuthorName() + " (" + comment.getTimestamp() + "):\n   " + comment.getContent();
                commentStrings.add(commentText);
            }
        }

        commentsListView.setItems(commentStrings);
        commentCountLabel.setText("Comments: " + comments.size());
    }

    @FXML
    private void handleLike() {
        System.out.println("=== LIKE BUTTON CLICKED ===");

        if (currentPost == null) {
            System.err.println("ERROR: No post loaded!");
            showAlert("Error", "No post loaded!");
            return;
        }

        // Add like to post
        currentPost.addLike();
        System.out.println("Like added. Total likes: " + currentPost.getLikeCount());

        // Update display
        likesLabel.setText("Likes: " + currentPost.getLikeCount());

        // Save to file
        Main.getSystemController().saveAll();
        System.out.println("Changes saved");

        showAlert("Success", "Post liked!");
    }

    @FXML
    private void handleAddComment() {
        System.out.println("=== ADD COMMENT BUTTON CLICKED ===");

        String content = commentArea.getText().trim();
        System.out.println("Comment content length: " + content.length());

        // Validate
        if (content.isEmpty()) {
            System.out.println("Comment is empty");
            showAlert("Error", "Comment cannot be empty!");
            return;
        }

        if (content.length() > 300) {
            System.out.println("Comment too long");
            showAlert("Error", "Comment too long! Maximum 300 characters.");
            return;
        }

        // Get current user
        User currentUser = Main.getSystemController()
                .getAuthManager()
                .getCurrentUser();

        if (currentUser == null) {
            System.err.println("ERROR: No user logged in!");
            showAlert("Error", "You must be logged in to comment!");
            return;
        }

        System.out.println("Creating comment for user: " + currentUser.getUsername());

        // Create comment
        Comment comment = new Comment(
                currentPost.getPostId(),
                currentUser.getUsername(),
                content
        );

        // Add to post and manager
        Main.getSystemController()
                .getPostManager()
                .addComment(comment);

        System.out.println("Comment added to post and manager");

        // Save
        Main.getSystemController().saveAll();
        System.out.println("Changes saved");

        // Clear text area
        commentArea.clear();

        // Refresh display
        loadComments();

        showAlert("Success", "Comment added!");
    }

    @FXML
    private void handleBack() {
        System.out.println("=== BACK BUTTON CLICKED ===");
        try {
            Main.showFeedScreen();
        } catch (Exception e) {
            System.err.println("Error returning to feed: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Cannot go back: " + e.getMessage());
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