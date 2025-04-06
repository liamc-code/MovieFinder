package com.example.moviefinder.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.moviefinder.model.Movie;
import com.example.moviefinder.model.MovieResponse;
import com.example.moviefinder.utils.ApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieViewModel extends ViewModel {
    // Declare the MutableLiveData field
    private final MutableLiveData<List<Movie>> movies = new MutableLiveData<>();
    private final MutableLiveData<List<Movie>> favoriteMovies = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationStatus = new MutableLiveData<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static final String TAG = "MovieViewModel";
    private static final String FAVORITES_COLLECTION = "favorites";

    // Public getter to expose LiveData (not MutableLiveData) to observers
    public LiveData<List<Movie>> getMovies() {
        return movies;
    }
    
    public LiveData<List<Movie>> getFavoriteMovies() {
        return favoriteMovies;
    }
    
    public LiveData<Boolean> getOperationStatus() {
        return operationStatus;
    }

    public void searchMovies(String query) {
        fetchMovies(query);
    }
    
    // Load favorite movies from Firestore for the current user
    public void loadFavoriteMovies() {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Log.e(TAG, "User not authenticated, cannot load favorites");
            favoriteMovies.postValue(new ArrayList<>());
            return;
        }
        
        db.collection(FAVORITES_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Movie> movieList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Movie movie = document.toObject(Movie.class);
                        movie.setDocumentId(document.getId());
                        movieList.add(movie);
                    }
                    Log.d(TAG, "Loaded " + movieList.size() + " favorite movies");
                    favoriteMovies.postValue(movieList);
                } else {
                    Log.e(TAG, "Error getting favorites", task.getException());
                    favoriteMovies.postValue(new ArrayList<>());
                }
            });
    }
    
    // Add a movie to favorites
    public void addToFavorites(Movie movie) {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Log.e(TAG, "User not authenticated, cannot add to favorites");
            operationStatus.postValue(false);
            return;
        }
        
        // Create a map of the movie data including the userId
        Map<String, Object> movieData = new HashMap<>();
        movieData.put("userId", userId);
        movieData.put("title", movie.getTitle());
        movieData.put("year", movie.getYear());
        movieData.put("posterUrl", movie.getPosterUrl());
        movieData.put("imdbID", movie.getImdbID());
        movieData.put("plot", movie.getPlot());
        movieData.put("director", movie.getDirector());
        movieData.put("actors", movie.getActors());
        movieData.put("studio", movie.getStudio());
        movieData.put("rating", movie.getRating());
        
        // Add the movie to Firestore
        db.collection(FAVORITES_COLLECTION)
            .add(movieData)
            .addOnSuccessListener(documentReference -> {
                Log.d(TAG, "Movie added to favorites with ID: " + documentReference.getId());
                operationStatus.postValue(true);
                loadFavoriteMovies(); // Refresh the favorites list
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error adding movie to favorites", e);
                operationStatus.postValue(false);
            });
    }
    
    // Update a favorite movie
    public void updateFavoriteMovie(Movie movie) {
        if (movie.getDocumentId() == null) {
            Log.e(TAG, "Cannot update movie without document ID");
            operationStatus.postValue(false);
            return;
        }
        
        DocumentReference movieRef = db.collection(FAVORITES_COLLECTION).document(movie.getDocumentId());
        
        // Create a map with the updated fields
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", movie.getTitle());
        updates.put("year", movie.getYear());
        updates.put("posterUrl", movie.getPosterUrl());
        updates.put("plot", movie.getPlot());
        updates.put("director", movie.getDirector());
        updates.put("actors", movie.getActors());
        updates.put("studio", movie.getStudio());
        updates.put("rating", movie.getRating());
        
        movieRef.update(updates)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Movie updated successfully");
                operationStatus.postValue(true);
                loadFavoriteMovies(); // Refresh the favorites list
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error updating movie", e);
                operationStatus.postValue(false);
            });
    }
    
    // Delete a favorite movie
    public void deleteFavoriteMovie(String documentId) {
        if (documentId == null) {
            Log.e(TAG, "Cannot delete movie without document ID");
            operationStatus.postValue(false);
            return;
        }
        
        db.collection(FAVORITES_COLLECTION).document(documentId)
            .delete()
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Movie deleted successfully");
                operationStatus.postValue(true);
                loadFavoriteMovies(); // Refresh the favorites list
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error deleting movie", e);
                operationStatus.postValue(false);
            });
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