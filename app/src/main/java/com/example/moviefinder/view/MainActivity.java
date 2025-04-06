package com.example.moviefinder.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.moviefinder.R;
import com.example.moviefinder.databinding.ActivityMainBinding;
import com.example.moviefinder.model.Movie;
import com.example.moviefinder.viewmodel.MovieViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MovieViewModel movieViewModel;
    private MyAdapter myAdapter;
    private static final String TAG = "MainActivity";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.movieRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        myAdapter = new MyAdapter(null);
        binding.movieRecyclerView.setAdapter(myAdapter);

        movieViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                if (movies != null && !movies.isEmpty()) {
                    Log.d(TAG, "Received " + movies.size() + " movies in MainActivity");
                    for (int i = 0; i < Math.min(movies.size(), 3); i++) {
                        Movie movie = movies.get(i);
                        Log.d(TAG, "Movie " + i + ": Title=" + movie.getTitle() + ", Year=" + movie.getYear() + ", Poster=" + movie.getPosterUrl());
                    }
                    myAdapter = new MyAdapter(movies);
                    binding.movieRecyclerView.setAdapter(myAdapter);
                } else {
                    Log.d(TAG, "No movies received in MainActivity");
                    Toast.makeText(MainActivity.this, "No movies found", Toast.LENGTH_SHORT).show();
                    myAdapter = new MyAdapter(null);
                    binding.movieRecyclerView.setAdapter(myAdapter);
                }
            }
        });

        // Observe operation status
        movieViewModel.getOperationStatus().observe(this, success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(MainActivity.this, "Movie added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to add movie", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Animation clickAnim = AnimationUtils.loadAnimation(this,R.anim.button_click);
        binding.searchButton.setOnClickListener(view -> {
            view.startAnimation(clickAnim);
            String query = binding.searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                movieViewModel.searchMovies(query);
            } else {
                Toast.makeText(this, "Please enter a movie name", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Set up navigation to favorites screen
        binding.favoritesButton.setOnClickListener(view -> {
            view.startAnimation(clickAnim);
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });
        
        // Set up search tab button
        binding.searchTabButton.setOnClickListener(view -> {
            view.startAnimation(clickAnim);
            // Already on search tab, do nothing
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle any activity results if needed
    }
}