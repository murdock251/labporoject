package com.example.ver3.controllers;

import com.example.ver3.Main;
import com.example.ver3.models.Post;
import com.example.ver3.models.User;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;

public class FeedController {
    @FXML
    private Label welcomeLabel;

    @FXML
    private Button viewProfileButton;

    @FXML
    private Button createPostButton;

    @FXML
    private VBox contentContainer;

    private ArrayList<Post> currentPosts;
    private static Post selectedPost;
    private boolean isProfileView = false;

    @FXML
    public void initialize() {
        System.out.println("=== FEED CONTROLLER INITIALIZE ===");

        User currentUser = Main.getSystemController().getAuthManager().getCurrentUser();

        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getName() + "!");
            System.out.println("User: " + currentUser.getName());
        }

        // Load feed view by default
        loadFeedView();
    }

    // ===== FEED VIEW =====
    private void loadFeedView() {
        System.out.println("=== LOADING FEED VIEW ===");
        isProfileView = false;

        contentContainer.getChildren().clear();

        currentPosts = Main.getSystemController().getPostManager().getAllPosts();
        System.out.println("Found " + currentPosts.size() + " posts");

        if (currentPosts.isEmpty()) {
            Label emptyLabel = new Label("No posts yet. Be the first to post!");
            emptyLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #65676b;");
            contentContainer.getChildren().add(emptyLabel);
        } else {
            // Reverse order so newest posts appear first
            for (int i = currentPosts.size() - 1; i >= 0; i--) {
                Post post = currentPosts.get(i);
                VBox postCard = createPostCard(post);
                contentContainer.getChildren().add(postCard);
            }
        }
    }

    // ===== PROFILE VIEW =====
    private void loadProfileView() {
        System.out.println("=== LOADING PROFILE VIEW ===");
        isProfileView = true;

        contentContainer.getChildren().clear();

        User currentUser = Main.getSystemController().getAuthManager().getCurrentUser();

        if (currentUser == null) {
            showAlert("Error", "No user logged in!");
            return;
        }

        // Profile Header Card
        VBox profileCard = new VBox(10);
        profileCard.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 8;");
        profileCard.setMaxWidth(600);

        // Back Button
        Button backButton = new Button("← Back to Feed");
        backButton.setStyle("-fx-background-color: #e4e6eb; -fx-font-weight: bold;");
        backButton.setOnAction(e -> loadFeedView());

        // Profile Title
        Label profileTitle = new Label("PROFILE");
        profileTitle.setFont(Font.font("System", FontWeight.BOLD, 24));

        Separator sep1 = new Separator();

        // User Info
        VBox userInfo = new VBox(8);
        userInfo.getChildren().addAll(
                createInfoRow("Username:", currentUser.getUsername()),
                createInfoRow("Name:", currentUser.getName()),
                createInfoRow("Email:", currentUser.getEmail()),
                createInfoRow("Phone:", currentUser.getPhoneNumber().isEmpty() ? "Not provided" : currentUser.getPhoneNumber()),
                createInfoRow("Address:", currentUser.getAddress().isEmpty() ? "Not provided" : currentUser.getAddress())
        );

        Separator sep2 = new Separator();

        // Stats
        int userPostCount = 0;
        for (Post post : Main.getSystemController().getPostManager().getAllPosts()) {
            if (post.getAuthorName().equals(currentUser.getUsername())) {
                userPostCount++;
            }
        }

        Label statsLabel = new Label("Total Posts: " + userPostCount);
        statsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        // Edit Profile Button
        Button editButton = new Button("✏️ Edit Profile");
        editButton.setStyle("-fx-background-color: #1877f2; -fx-text-fill: white; -fx-font-weight: bold;");
        editButton.setOnAction(e -> handleEditProfile());

        profileCard.getChildren().addAll(backButton, profileTitle, sep1, userInfo, sep2, statsLabel, editButton);

        contentContainer.getChildren().add(profileCard);

        // My Posts Section
        Label myPostsLabel = new Label("MY POSTS");
        myPostsLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        myPostsLabel.setStyle("-fx-padding: 20 0 10 0;");
        contentContainer.getChildren().add(myPostsLabel);

        // Show only user's posts
        ArrayList<Post> allPosts = Main.getSystemController().getPostManager().getAllPosts();
        boolean hasUserPosts = false;

        for (int i = allPosts.size() - 1; i >= 0; i--) {
            Post post = allPosts.get(i);
            if (post.getAuthorName().equals(currentUser.getUsername())) {
                VBox postCard = createPostCard(post);
                contentContainer.getChildren().add(postCard);
                hasUserPosts = true;
            }
        }

        if (!hasUserPosts) {
            Label noPosts = new Label("You haven't posted anything yet.");
            noPosts.setStyle("-fx-font-size: 14; -fx-text-fill: #65676b;");
            contentContainer.getChildren().add(noPosts);
        }
    }

    // Helper to create info rows for profile
    private HBox createInfoRow(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label labelText = new Label(label);
        labelText.setFont(Font.font("System", FontWeight.BOLD, 14));
        labelText.setMinWidth(100);

        Label valueText = new Label(value);
        valueText.setFont(Font.font("System", 14));

        row.getChildren().addAll(labelText, valueText);
        return row;
    }

    // ===== CREATE POST CARD =====
    private VBox createPostCard(Post post) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #e4e6eb; -fx-border-radius: 8;");
        card.setMaxWidth(600);

        // Author and timestamp
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label authorLabel = new Label("👤 " + post.getAuthorName());
        authorLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label timeLabel = new Label(post.getTimestamp());
        timeLabel.setStyle("-fx-text-fill: #65676b; -fx-font-size: 12;");

        header.getChildren().addAll(authorLabel, spacer, timeLabel);

        // Post type badge
        Label typeLabel = new Label(post.getPostType());
        typeLabel.setStyle("-fx-background-color: #e4e6eb; -fx-padding: 3 8; -fx-background-radius: 4; -fx-font-size: 11;");

        // Content with "See More" functionality
        Label contentLabel = new Label();
        contentLabel.setWrapText(true);
        contentLabel.setFont(Font.font("System", 14));

        String fullContent = post.getContent();
        boolean needsTruncation = fullContent.length() > 150;

        if (needsTruncation) {
            contentLabel.setText(fullContent.substring(0, 150) + "...");

            Hyperlink seeMoreLink = new Hyperlink("See More");
            seeMoreLink.setStyle("-fx-font-size: 13; -fx-text-fill: #1877f2;");

            final boolean[] isExpanded = {false};

            seeMoreLink.setOnAction(e -> {
                if (isExpanded[0]) {
                    contentLabel.setText(fullContent.substring(0, 150) + "...");
                    seeMoreLink.setText("See More");
                    isExpanded[0] = false;
                } else {
                    contentLabel.setText(fullContent);
                    seeMoreLink.setText("See Less");
                    isExpanded[0] = true;
                }
            });

            VBox contentBox = new VBox(5, contentLabel, seeMoreLink);
            card.getChildren().addAll(header, typeLabel, contentBox);
        } else {
            contentLabel.setText(fullContent);
            card.getChildren().addAll(header, typeLabel, contentLabel);
        }

        Separator separator = new Separator();

        // Like and Comment counts
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER_LEFT);

        Label likesLabel = new Label("👍 " + post.getLikeCount() + " Likes");
        likesLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

        Label commentsLabel = new Label("💬 " + post.getCommentCount() + " Comments");
        commentsLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

        statsBox.getChildren().addAll(likesLabel, commentsLabel);

        // Action buttons
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER);

        Button likeButton = new Button("👍 Like");
        likeButton.setStyle("-fx-background-color: #e4e6eb; -fx-font-weight: bold; -fx-cursor: hand;");
        likeButton.setPrefWidth(150);
        likeButton.setOnAction(e -> handleLikeFromFeed(post, likesLabel, card));

        Button commentButton = new Button("💬 Comment");
        commentButton.setStyle("-fx-background-color: #e4e6eb; -fx-font-weight: bold; -fx-cursor: hand;");
        commentButton.setPrefWidth(150);
        commentButton.setOnAction(e -> handleCommentClick(post));

        actionsBox.getChildren().addAll(likeButton, commentButton);

        card.getChildren().addAll(separator, statsBox, actionsBox);

        return card;
    }

    // ===== EVENT HANDLERS =====

    @FXML
    private void handleCreatePost() {
        System.out.println("=== CREATE POST BUTTON CLICKED ===");
        try {
            Main.showCreatePostScreen();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Cannot open create post screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewProfile() {
        System.out.println("=== VIEW PROFILE BUTTON CLICKED ===");
        loadProfileView();
    }

    @FXML
    private void handleRefresh() {
        System.out.println("=== REFRESH BUTTON CLICKED ===");
        if (isProfileView) {
            loadProfileView();
        } else {
            loadFeedView();
        }
        showAlert("Refreshed", "Feed refreshed successfully!");
    }

    @FXML
    private void handleLogout() {
        System.out.println("=== LOGOUT BUTTON CLICKED ===");
        Main.getSystemController().getAuthManager().logout();
        Main.getSystemController().saveAll();

        try {
            Main.showLoginScreen();
        } catch (Exception e) {
            showAlert("Error", "Cannot logout: " + e.getMessage());
        }
    }

    private void handleLikeFromFeed(Post post, Label likesLabel, VBox card) {
        System.out.println("=== LIKE BUTTON CLICKED ON FEED ===");

        post.addLike();
        likesLabel.setText("👍 " + post.getLikeCount() + " Likes");

        Main.getSystemController().saveAll();
        System.out.println("Post liked! New count: " + post.getLikeCount());

        // Visual feedback - briefly change button color
        Button likeButton = null;
        for (javafx.scene.Node node : ((HBox)card.getChildren().get(card.getChildren().size()-1)).getChildren()) {
            if (node instanceof Button && ((Button)node).getText().contains("Like")) {
                likeButton = (Button)node;
                break;
            }
        }

        if (likeButton != null) {
            final Button btn = likeButton;
            String originalStyle = btn.getStyle();
            btn.setStyle("-fx-background-color: #1877f2; -fx-text-fill: white; -fx-font-weight: bold;");

            new Thread(() -> {
                try {
                    Thread.sleep(300);
                    javafx.application.Platform.runLater(() -> btn.setStyle(originalStyle));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void handleCommentClick(Post post) {
        System.out.println("=== COMMENT BUTTON CLICKED ===");
        selectedPost = post;

        try {
            Main.showPostDetailScreen();
        } catch (Exception e) {
            showAlert("Error", "Cannot open post details: " + e.getMessage());
        }
    }

    private void handleEditProfile() {
        System.out.println("=== EDIT PROFILE BUTTON CLICKED ===");
        try {
            Main.showEditProfileScreen();
        } catch (Exception e) {
            showAlert("Error", "Cannot open edit profile: " + e.getMessage());
        }
    }

    public static Post getSelectedPost() {
        return selectedPost;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}