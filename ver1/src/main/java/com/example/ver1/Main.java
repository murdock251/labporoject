package com.example.ver1;

import com.example.ver1.models.*;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        User u1 = new User("U25128","nafis32","icyquail947","nafismondol55@gmail.com");

        System.out.println(u1);

        User u2 = new User("nafis34","icyquail947","nafis251@gmail.com");
        System.out.println(u2);
        User u3 = new User("nafis34","icyquail947","nafis251@gmail.com");
        System.out.println(u3);

        Comment c1 = new Comment("P123","nafis_noor","This is my first post");
        c1.display();

        Post p1 = new TextPost("john_doe","Hello World! This is my first post.");
        p1.addLike(); p1.addLike(); p1.addLike();
//        System.out.println("Likes: " + p1.getLikeCount());
        p1.display();

        Post p2 = new StatusPost("nafis","Having a great day!","Happy");
        p2.addLike(); p2.addLike();
        p2.display();

        ArrayList<Post>posts = new ArrayList<>();
        posts.add(p1);
        posts.add(p2);

        for(Post p : posts){
            p.display();
        }

        UserManager um = new UserManager();
        um.addUser(u1);
        um.addUser(u2);

        System.out.println("Total users: " + um.getUserCount());
        System.out.println("Find nafis34: " + um.getUserByUsername("nafis34"));
        System.out.println("Username exists: " + um.usernameExists("nafis34"));

        PostManager pm = new PostManager();

        Post p3 = pm.createPost("Hello everyone!!!","nafis");
        Post p4 = pm.createPost("Great day everyone!!!!","jane","Happy");

        p3.display();
        p4.display();
    }
}
