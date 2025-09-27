package com.example.android.model;

import com.google.gson.annotations.SerializedName;

public class Quote {
    @SerializedName("q")
    private String quoteText;

    @SerializedName("a")
    private String author;

    // Getters y setters
    public String getQuoteText() {
        return quoteText;
    }

    public void setQuoteText(String quoteText) {
        this.quoteText = quoteText;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
