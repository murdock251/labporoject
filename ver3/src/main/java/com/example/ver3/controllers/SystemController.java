package com.example.ver3.controllers;

import com.example.ver3.models.*;

import java.io.IOException;
import java.util.ArrayList;

public class SystemController {
    private AuthManager authManager;
    private UserManager userManager;
    private PostManager postManager;

    public SystemController(){
        this.userManager = new UserManager();
        this.postManager = new PostManager();
        this.authManager = new AuthManager(userManager);
    }

    public void initialize(){
        System.out.println("=== System initialized ====");

        try{
            ArrayList<User> users = FileManager.loadUsers();
            userManager.setUsers(users);

            ArrayList<Post> posts = FileManager.loadPosts();
            postManager.setPosts(posts);

            ArrayList<Comment> comments = FileManager.loadComments();
            postManager.setComments(comments);

            for(Comment comment : comments){
                Post post = postManager.getPostById(comment.getPostId());
                if(post!=null){
                    post.addComment(comment);
                }
            }

            System.out.println("System ready");
        }
        catch(IOException e){
            System.err.println("Error loading data: " + e.getMessage());
        }
        catch (NumberFormatException e){
            System.err.println("Corrupted data file: " + e.getMessage());
        } finally {
            System.out.println("Initialization complete");
        }
    }

    public void saveAll(){
        System.out.println("=== Saving data ===");
        try{
            FileManager.saveUsers(userManager.getAllUsers());
            FileManager.savePosts(postManager.getAllPosts());
            FileManager.saveComments(postManager.getAllComments());
        } catch (IOException e){
            System.err.println("Error saving data: " + e.getMessage());
        } finally {
            System.out.println("Save operation complete");
        }


    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public PostManager getPostManager() {
        return postManager;
    }
    public UserManager getUserManager() {
        return userManager;
    }

    //Testing

//    public static void main(String[] args) {
//        SystemController system = new SystemController();
//        system.initialize();
//
//        try{
//            system.getAuthManager().register("john","pass123","john@gmail.com");
//            system.getAuthManager().register("nafis","nafis45a","nafis55@gmail.com");
//        }
//        catch(DuplicateUserException e){
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//        }
//
//        try{
//            system.getAuthManager().login("john","pass123");
//        }
//        catch (InvalidLoginException e){
//            System.out.println(e.getMessage());
//        }

//        system.getPostManager().createPost("Hello this is noor rasika","nofis");
//
//
//
//        system.saveAll();
//        System.out.println("\n=== CLOSING APP===\n");

//        SystemController system2 = new SystemController();
//        system2.initialize();
//        System.out.println("User loaded: " + system2.getUserManager().getUserCount());
//        System.out.println("Post loader: " + system2.getPostManager().getAllPosts().size());
//
//        for(Post post: system2.getPostManager().getAllPosts()){
//            post.display();
//        }
//    }


}
