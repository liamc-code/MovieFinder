package com.example.moviefinder.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviefinder.R;
import com.example.moviefinder.databinding.ActivityMainBinding;
import com.example.moviefinder.model.Movie;
import com.example.moviefinder.viewmodel.MovieViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // ViewBinding instance to access views directly
    private ActivityMainBinding binding;

    // Declare ViewModel
    private MovieViewModel movieViewModel;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up RecyclerView
        binding.movieRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ViewModel
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        // Observe movie data from ViewModel
        movieViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                // Log the received movie list for debugging
                if (movies != null && !movies.isEmpty()) {
                    Log.d("MainActivity", "Received " + movies.size() + " movies in MainActivity");
                    // Populate RecyclerView with the movie list
                    myAdapter = new MyAdapter(movies);
                    binding.movieRecyclerView.setAdapter(myAdapter);
                } else {
                    Log.d("MainActivity", "No movies received in MainActivity");
                    Toast.makeText(MainActivity.this, "No movies found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up the search button click listener
        binding.searchButton.setOnClickListener(view -> {
            String query = binding.searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                // Fetch movies using ViewModel
                movieViewModel.searchMovies(query);
            } else {
                Toast.makeText(this, "Please enter a movie name", Toast.LENGTH_SHORT).show();
            }
        });
    }
}