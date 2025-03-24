package com.example.moviefinder.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.moviefinder.model.Movie;
import com.example.moviefinder.model.MovieResponse;
import com.example.moviefinder.utils.ApiClient;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;

public class MovieViewModel extends ViewModel {
    // Declare the MutableLiveData field
    private final MutableLiveData<List<Movie>> movies = new MutableLiveData<>();

    // Public getter to expose LiveData (not MutableLiveData) to observers
    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public void searchMovies(String query) {
        fetchMovies(query);
    }
    private void fetchMovies(String query) {
        String apiKey = "21468ff5"; // Replace with your actual API key

        new Thread(() -> {
            try {
                // Fetch the JSON response from the API
                String jsonResponse = ApiClient.getMovies(query, apiKey);

                // Log the raw response for debugging
                Log.d("API Response", "Raw Response: " + jsonResponse);

                // Parse the JSON response using Gson
                Gson gson = new Gson();
                MovieResponse movieResponse = gson.fromJson(jsonResponse, MovieResponse.class);

                // Log the parsed response for verification
                if (movieResponse != null && movieResponse.getSearch() != null) {
                    List<Movie> movieList = movieResponse.getSearch();

                    // Log the movies to verify data
                    Log.d("API Response", "Movies found: " + movieList.size());

                    // Ensure that the posterUrl is set correctly by calling the setter
                    for (Movie movie : movieList) {
                        Log.d("Movie", "Setting poster URL: " + movie.getPosterUrl());  // Verify poster URL
                        movie.setPosterUrl(movie.getPosterUrl());
                    }

                    // Update LiveData with the movie list
                    movies.postValue(movieList); // This should now work
                } else {
                    Log.d("API Response", "No movies found in response");
                    movies.postValue(null); // Trigger "No movies found" in MainActivity
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("API Response", "Network error: " + e.getMessage());
                movies.postValue(null); // Trigger error handling in MainActivity
            }
        }).start();
    }


}