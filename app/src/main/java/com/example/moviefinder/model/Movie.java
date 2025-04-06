package com.example.moviefinder.model;

import android.util.Log;
import com.google.gson.annotations.SerializedName;

public class Movie {
    @SerializedName("Title")
    private String title;

    @SerializedName("Year")
    private String year;

    @SerializedName("Poster")
    private String posterUrl;

    @SerializedName("imdbID")
    private String imdbID;

    @SerializedName("Plot")
    private String plot;

    @SerializedName("Director")
    private String director;

    @SerializedName("Actors")
    private String actors;
    
    // OMDB API do have a field for studio.
    // So for future proofing this field has been added.
    private String studio;
    private String rating;
    

    private String documentId;


    public Movie() {
        // Required empty constructor for Firestore.
        // Firestore uses it to create an empty Movie object before populating it with data from the database.
    }

    // Constructor
    public Movie(String title, String year, String posterUrl) {
        this.title = title;
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
        Log.d("Movie", "Setting poster URL: " + posterUrl);
        this.posterUrl = posterUrl;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
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
    
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}