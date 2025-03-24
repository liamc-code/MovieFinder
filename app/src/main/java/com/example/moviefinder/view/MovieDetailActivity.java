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

import com.example.moviefinder.R;
import com.example.moviefinder.model.Movie;
import com.example.moviefinder.utils.ApiClient;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView titleTextView, yearTextView, plotTextView, directorTextView, actorsTextView;
    private ImageView posterImageView;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Initialize views
        titleTextView = findViewById(R.id.detailTitleTextView);
        yearTextView = findViewById(R.id.detailYearTextView);
        plotTextView = findViewById(R.id.detailPlotTextView);
        directorTextView = findViewById(R.id.detailDirectorTextView);
        actorsTextView = findViewById(R.id.detailActorsTextView);
        posterImageView = findViewById(R.id.detailPosterImageView);
        backButton = findViewById(R.id.backButton);

        // Add click listener for the back button (and animation)
        Animation clickAnim = AnimationUtils.loadAnimation(this, R.anim.button_click);
        backButton.setOnClickListener(v -> {
            v.startAnimation(clickAnim);
            finish();
        });

        // Get the imdbID from the intent
        String imdbID = getIntent().getStringExtra("imdbID");
        if (imdbID == null) {
            Toast.makeText(this, "Movie ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch detailed movie data
        fetchMovieDetails(imdbID);
    }

    private void fetchMovieDetails(String imdbID) {
        new Thread(() -> {
            try {
                String apiKey = "7a169c"; //
                String jsonResponse = ApiClient.getMovieDetails(imdbID, apiKey);

                // Log the response for debugging
                Log.d("Detail API Response", "Raw Response: " + jsonResponse);

                // Parse the JSON response
                Gson gson = new Gson();
                Movie movie = gson.fromJson(jsonResponse, Movie.class);

                // Update UI on the main thread
                runOnUiThread(() -> {
                    if (movie != null) {
                        titleTextView.setText(movie.getTitle() != null ? movie.getTitle() : "No Title");
                        yearTextView.setText(movie.getYear() != null ? movie.getYear() : "No Year");
                        plotTextView.setText(movie.getPlot() != null ? movie.getPlot() : "No Plot");
                        directorTextView.setText(movie.getDirector() != null ? movie.getDirector() : "No Director");
                        actorsTextView.setText(movie.getActors() != null ? movie.getActors() : "No Actors");

                        String posterUrl = movie.getPosterUrl();
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
}