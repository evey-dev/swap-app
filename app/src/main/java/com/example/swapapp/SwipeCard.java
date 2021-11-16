package com.example.swapapp;

public class SwipeCard {
    private String text1, id;

    public SwipeCard(String text1, String id) {
        this.text1 = text1;
        this.id = id;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}