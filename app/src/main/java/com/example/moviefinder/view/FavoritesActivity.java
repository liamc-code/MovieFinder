package com.example.moviefinder.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.moviefinder.R;
import com.example.moviefinder.databinding.ActivityFavoritesBinding;
import com.example.moviefinder.model.Movie;
import com.example.moviefinder.viewmodel.MovieViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements FavoritesAdapter.OnFavoriteActionListener {

    private ActivityFavoritesBinding binding;
    private MovieViewModel movieViewModel;
    private FavoritesAdapter favoritesAdapter;
    private static final String TAG = "FavoritesActivity";
    private static final int EDIT_MOVIE_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up RecyclerView
        binding.favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoritesAdapter = new FavoritesAdapter(new ArrayList<>(), this);
        binding.favoritesRecyclerView.setAdapter(favoritesAdapter);

        // Set up ViewModel
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        
        // Observe favorite movies from the ViewModel
        movieViewModel.getFavoriteMovies().observe(this, movies -> {
            if (movies != null) {
                Log.d(TAG, "Received " + movies.size() + " favorite movies");
                favoritesAdapter.updateData(movies);
                
                if (movies.isEmpty()) {
                    Toast.makeText(this, "No favorite movies found", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        // Observe operation status
        movieViewModel.getOperationStatus().observe(this, success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(this, "Operation completed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Operation failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Load favorite movies
        movieViewModel.loadFavoriteMovies();
        
        // Set up navigation buttons
        Animation clickAnim = AnimationUtils.loadAnimation(this, R.anim.button_click);
        
        binding.searchTabButton.setOnClickListener(v -> {
            v.startAnimation(clickAnim);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the favorites list when returning to this activity
        movieViewModel.loadFavoriteMovies();
    }

    @Override
    public void onEdit(Movie movie) {
        Intent intent = new Intent(this, MovieEditActivity.class);
        intent.putExtra("MOVIE_ID", movie.getDocumentId());
        intent.putExtra("MOVIE_TITLE", movie.getTitle());
        intent.putExtra("MOVIE_YEAR", movie.getYear());
        intent.putExtra("MOVIE_POSTER", movie.getPosterUrl());
        intent.putExtra("MOVIE_PLOT", movie.getPlot());
        intent.putExtra("MOVIE_DIRECTOR", movie.getDirector());
        intent.putExtra("MOVIE_ACTORS", movie.getActors());
        intent.putExtra("MOVIE_STUDIO", movie.getStudio());
        intent.putExtra("MOVIE_RATING", movie.getRating());
        intent.putExtra("IS_EDIT_MODE", true);
        startActivityForResult(intent, EDIT_MOVIE_REQUEST);
    }

    @Override
    public void onDelete(Movie movie) {
        new AlertDialog.Builder(this)
                .setTitle("Delete " + movie.getTitle())
                .setMessage("Are you sure you want to delete this movie from your favorites?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    movieViewModel.deleteFavoriteMovie(movie.getDocumentId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onItemClick(Movie movie) {
        Intent intent = new Intent(this, FavoriteDetailActivity.class);
        intent.putExtra("MOVIE_ID", movie.getDocumentId());
        intent.putExtra("MOVIE_TITLE", movie.getTitle());
        intent.putExtra("MOVIE_YEAR", movie.getYear());
        intent.putExtra("MOVIE_POSTER", movie.getPosterUrl());
        intent.putExtra("MOVIE_PLOT", movie.getPlot());
        intent.putExtra("MOVIE_DIRECTOR", movie.getDirector());
        intent.putExtra("MOVIE_ACTORS", movie.getActors());
        intent.putExtra("MOVIE_STUDIO", movie.getStudio());
        intent.putExtra("MOVIE_RATING", movie.getRating());
        startActivity(intent);
    }
} 