package com.example.moviefinder.view;

import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.moviefinder.R;
import com.example.moviefinder.databinding.ActivityMovieEditBinding;
import com.example.moviefinder.model.Movie;
import com.example.moviefinder.viewmodel.MovieViewModel;
import com.squareup.picasso.Picasso;

public class MovieEditActivity extends AppCompatActivity {

    private ActivityMovieEditBinding binding;
    private MovieViewModel movieViewModel;
    private boolean isEditMode = false;
    private String movieId;
    private static final String TAG = "MovieEditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up ViewModel
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        // Check if we're in edit mode
        if (getIntent().hasExtra("IS_EDIT_MODE")) {
            isEditMode = getIntent().getBooleanExtra("IS_EDIT_MODE", false);
        }

        // Set the title based on the mode
        if (isEditMode) {
            binding.editScreenTitle.setText("Edit Movie");
            binding.saveButton.setText("Update");
            loadMovieData();
        } else {
            binding.editScreenTitle.setText("Add Movie");
            binding.saveButton.setText("Add");
        }

        // Set up button click listeners
        Animation clickAnim = AnimationUtils.loadAnimation(this, R.anim.button_click);
        
        binding.saveButton.setOnClickListener(v -> {
            v.startAnimation(clickAnim);
            if (validateInputs()) {
                saveMovie();
            }
        });
        
        binding.cancelButton.setOnClickListener(v -> {
            v.startAnimation(clickAnim);
            finish();
        });
        
        // Observe operation status
        movieViewModel.getOperationStatus().observe(this, success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(this, isEditMode ? "Movie updated successfully" : "Movie added successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Operation failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadMovieData() {
        // Populate fields with existing movie data
        if (getIntent().hasExtra("MOVIE_ID")) {
            movieId = getIntent().getStringExtra("MOVIE_ID");
            binding.movieTitleInput.setText(getIntent().getStringExtra("MOVIE_TITLE"));
            binding.movieYearInput.setText(getIntent().getStringExtra("MOVIE_YEAR"));
            binding.movieStudioInput.setText(getIntent().getStringExtra("MOVIE_STUDIO"));
            binding.movieRatingInput.setText(getIntent().getStringExtra("MOVIE_RATING"));
            binding.moviePlotInput.setText(getIntent().getStringExtra("MOVIE_PLOT"));
            
            String posterUrl = getIntent().getStringExtra("MOVIE_POSTER");
            binding.moviePosterUrlInput.setText(posterUrl);
            
            // Load image preview if URL is valid
            if (posterUrl != null && !posterUrl.isEmpty() && !posterUrl.equals("N/A")) {
                Picasso.get()
                        .load(posterUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(binding.movieImagePreview);
            }
        }
    }

    private boolean validateInputs() {
        // Validate required fields
        if (binding.movieTitleInput.getText().toString().trim().isEmpty()) {
            binding.movieTitleInput.setError("Title is required");
            return false;
        }
        
        if (binding.movieYearInput.getText().toString().trim().isEmpty()) {
            binding.movieYearInput.setError("Year is required");
            return false;
        }
        
        if (binding.movieStudioInput.getText().toString().trim().isEmpty()) {
            binding.movieStudioInput.setError("Studio is required");
            return false;
        }
        
        if (binding.movieRatingInput.getText().toString().trim().isEmpty()) {
            binding.movieRatingInput.setError("Rating is required");
            return false;
        }
        
        return true;
    }

    private void saveMovie() {
        // Create a Movie object with the input data
        Movie movie = new Movie();
        movie.setTitle(binding.movieTitleInput.getText().toString().trim());
        movie.setYear(binding.movieYearInput.getText().toString().trim());
        movie.setStudio(binding.movieStudioInput.getText().toString().trim());
        movie.setRating(binding.movieRatingInput.getText().toString().trim());
        movie.setPlot(binding.moviePlotInput.getText().toString().trim());
        movie.setPosterUrl(binding.moviePosterUrlInput.getText().toString().trim());
        
        if (isEditMode) {
            // Set the document ID for updating
            movie.setDocumentId(movieId);
            movieViewModel.updateFavoriteMovie(movie);
        } else {
            // Add new movie to favorites
            movieViewModel.addToFavorites(movie);
        }
    }
} 