package com.example.moviefinder.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieResponse {

    // The "Search" field in the JSON response maps to this field
    @SerializedName("Search")
    private List<Movie> search;

    // Getter method to retrieve the list of movies
    public List<Movie> getSearch() {
        return search;
    }

    // Setter method to set the list of movies (optional if you're not using Gson's default parsing)
    public void setSearch(List<Movie> search) {
        this.search = search;
    }

    // Add a method to check if the response is valid
    public boolean isValid() {
        return search != null && !search.isEmpty();
    }
}
