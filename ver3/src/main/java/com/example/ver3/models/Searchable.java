package com.example.ver3.models;

import java.util.ArrayList;

public interface Searchable {
    ArrayList<Post> search(String keyword);
}
