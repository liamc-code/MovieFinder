package com.example.moviefinder.model;

public class Movie {
    private String title;
    private String studio;
    private String rating;
    private String year;
    private String posterUrl;  // Add this field for the movie poster URL

    // Constructor
    public Movie(String title, String studio, String rating, String year, String posterUrl) {
        this.title = title;
        this.studio = studio;
        this.rating = rating;
        this.year = year;
        this.posterUrl = posterUrl;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}
