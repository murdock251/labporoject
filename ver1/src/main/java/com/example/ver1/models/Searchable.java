package com.example.ver1.models;

import java.util.ArrayList;

public interface Searchable {
    ArrayList<Post> search(String keyword);
}
