package com.example.moviefinder.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.moviefinder.R;
import com.example.moviefinder.databinding.ActivityFavoriteDetailBinding;
import com.example.moviefinder.viewmodel.MovieViewModel;
import com.squareup.picasso.Picasso;

public class FavoriteDetailActivity extends AppCompatActivity {

    private ActivityFavoriteDetailBinding binding;
    private MovieViewModel movieViewModel;
    private String movieId;
    private static final int EDIT_MOVIE_REQUEST = 101;
    private static final String TAG = "FavoriteDetailActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up ViewModel
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        // Get movie data from intent
        if (getIntent().hasExtra("MOVIE_ID")) {
            movieId = getIntent().getStringExtra("MOVIE_ID");
            displayMovieDetails();
        } else {
            Toast.makeText(this, "Movie details not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up button click listeners
        Animation clickAnim = AnimationUtils.loadAnimation(this, R.anim.button_click);
        
        binding.backButton.setOnClickListener(v -> {
            v.startAnimation(clickAnim);
            finish();
        });
        
        binding.updateButton.setOnClickListener(v -> {
            v.startAnimation(clickAnim);
            openEditScreen();
        });
        
        binding.deleteButton.setOnClickListener(v -> {
            v.startAnimation(clickAnim);
            confirmDelete();
        });
        
        // Observe operation status for delete operation
        movieViewModel.getOperationStatus().observe(this, success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(this, "Movie deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }



    private void displayMovieDetails() {
        // Set the title
        binding.detailTitle.setText(getIntent().getStringExtra("MOVIE_TITLE"));
        
        // Set other text fields
        binding.detailYearTextView.setText(getIntent().getStringExtra("MOVIE_YEAR"));
        binding.detailPlotTextView.setText(getIntent().getStringExtra("MOVIE_PLOT"));
        binding.detailDirectorTextView.setText(getIntent().getStringExtra("MOVIE_DIRECTOR"));
        binding.detailActorsTextView.setText(getIntent().getStringExtra("MOVIE_ACTORS"));
        binding.detailStudioTextView.setText(getIntent().getStringExtra("MOVIE_STUDIO"));
        binding.detailRatingTextView.setText(getIntent().getStringExtra("MOVIE_RATING"));
        
        // Load poster image if available
        String posterUrl = getIntent().getStringExtra("MOVIE_POSTER");
        if (posterUrl != null && !posterUrl.isEmpty() && !posterUrl.equals("N/A")) {
            Picasso.get()
                    .load(posterUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(binding.detailPosterImageView);
        } else {
            binding.detailPosterImageView.setImageResource(R.drawable.placeholder_image);
        }
    }



    private void openEditScreen() {
        Intent intent = new Intent(this, MovieEditActivity.class);
        intent.putExtra("IS_EDIT_MODE", true);
        intent.putExtra("MOVIE_ID", movieId);
        intent.putExtra("MOVIE_TITLE", getIntent().getStringExtra("MOVIE_TITLE"));
        intent.putExtra("MOVIE_YEAR", getIntent().getStringExtra("MOVIE_YEAR"));
        intent.putExtra("MOVIE_POSTER", getIntent().getStringExtra("MOVIE_POSTER"));
        intent.putExtra("MOVIE_PLOT", getIntent().getStringExtra("MOVIE_PLOT"));
        intent.putExtra("MOVIE_DIRECTOR", getIntent().getStringExtra("MOVIE_DIRECTOR"));
        intent.putExtra("MOVIE_ACTORS", getIntent().getStringExtra("MOVIE_ACTORS"));
        intent.putExtra("MOVIE_STUDIO", getIntent().getStringExtra("MOVIE_STUDIO"));
        intent.putExtra("MOVIE_RATING", getIntent().getStringExtra("MOVIE_RATING"));
        startActivityForResult(intent, EDIT_MOVIE_REQUEST);
    }



    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete " + getIntent().getStringExtra("MOVIE_TITLE"))
                .setMessage("Are you sure you want to delete this movie from your favorites?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    movieViewModel.deleteFavoriteMovie(movieId);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == EDIT_MOVIE_REQUEST && resultCode == RESULT_OK) {
            // Movie was updated, refresh the details
            finish();
        }
    }
} 