package com.example.moviefinder.view;

import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.moviefinder.R;
import com.example.moviefinder.model.Movie;
import com.example.moviefinder.utils.ApiClient;
import com.example.moviefinder.viewmodel.MovieViewModel;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView titleTextView, yearTextView, plotTextView, directorTextView, actorsTextView;
    private ImageView posterImageView;
    private Button backButton, addToFavButton;
    private Movie currentMovie;
    private MovieViewModel movieViewModel;
    private static final String TAG = "MovieDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Initialize ViewModel
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        // Initialize views
        titleTextView = findViewById(R.id.detailTitleTextView);
        yearTextView = findViewById(R.id.detailYearTextView);
        plotTextView = findViewById(R.id.detailPlotTextView);
        directorTextView = findViewById(R.id.detailDirectorTextView);
        actorsTextView = findViewById(R.id.detailActorsTextView);
        posterImageView = findViewById(R.id.detailPosterImageView);
        backButton = findViewById(R.id.backButton);
        addToFavButton = findViewById(R.id.addToFavButton);

        // Initialize animation
        Animation clickAnim = AnimationUtils.loadAnimation(this, R.anim.button_click);
        
        // Add click listener for the back button
        backButton.setOnClickListener(v -> {
            v.startAnimation(clickAnim);
            finish();
        });
        
        // Add click listener for the add to favorites button
        addToFavButton.setOnClickListener(v -> {
            v.startAnimation(clickAnim);
            addToFavorites();
        });

        // Get the imdbID from the intent
        String imdbID = getIntent().getStringExtra("imdbID");
        if (imdbID == null) {
            Toast.makeText(this, "Movie ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Observe operation status
        movieViewModel.getOperationStatus().observe(this, success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(this, "Movie added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to add movie to favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Fetch detailed movie data
        fetchMovieDetails(imdbID);
    }

    private void fetchMovieDetails(String imdbID) {
        new Thread(() -> {
            try {
                String apiKey = "7a169c"; //
                String jsonResponse = ApiClient.getMovieDetails(imdbID, apiKey);

                // Log the response for debugging
                Log.d(TAG, "Raw Response: " + jsonResponse);

                // Parse the JSON response
                Gson gson = new Gson();
                currentMovie = gson.fromJson(jsonResponse, Movie.class);

                // Update UI on the main thread
                runOnUiThread(() -> {
                    if (currentMovie != null) {
                        titleTextView.setText(currentMovie.getTitle() != null ? currentMovie.getTitle() : "No Title");
                        yearTextView.setText(currentMovie.getYear() != null ? currentMovie.getYear() : "No Year");
                        plotTextView.setText(currentMovie.getPlot() != null ? currentMovie.getPlot() : "No Plot");
                        directorTextView.setText(currentMovie.getDirector() != null ? currentMovie.getDirector() : "No Director");
                        actorsTextView.setText(currentMovie.getActors() != null ? currentMovie.getActors() : "No Actors");

                        String posterUrl = currentMovie.getPosterUrl();
                        if (posterUrl != null && !posterUrl.equals("N/A")) {
                            if (posterUrl.startsWith("http://")) {
                                posterUrl = posterUrl.replace("http://", "https://");
                            }
                            Picasso.get()
                                    .load(posterUrl)
                                    .placeholder(R.drawable.placeholder_image)
                                    .error(R.drawable.error_image)
                                    .into(posterImageView);
                        } else {
                            posterImageView.setImageResource(R.drawable.placeholder_image);
                        }
                    } else {
                        Toast.makeText(MovieDetailActivity.this, "Failed to load movie details", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MovieDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
    
    private void addToFavorites() {
        if (currentMovie == null) {
            Toast.makeText(this, "No movie details available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Set default values for studio and rating if empty
        if (currentMovie.getStudio() == null || currentMovie.getStudio().isEmpty()) {
            currentMovie.setStudio(currentMovie.getDirector() != null ? currentMovie.getDirector() : "Unknown Studio");
        }
        
        if (currentMovie.getRating() == null || currentMovie.getRating().isEmpty()) {
            currentMovie.setRating("Not Rated");
        }
        
        // Add the movie to favorites
        movieViewModel.addToFavorites(currentMovie);
    }
}