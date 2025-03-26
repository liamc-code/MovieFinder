package com.example.moviefinder.view;

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
                    Log.d("MainActivity", "Received " + movies.size() + " movies in MainActivity");
                    for (int i = 0; i < Math.min(movies.size(), 3); i++) {
                        Movie movie = movies.get(i);
                        Log.d("MainActivity", "Movie " + i + ": Title=" + movie.getTitle() + ", Year=" + movie.getYear() + ", Poster=" + movie.getPosterUrl());
                    }
                    myAdapter = new MyAdapter(movies);
                    binding.movieRecyclerView.setAdapter(myAdapter);
                } else {
                    Log.d("MainActivity", "No movies received in MainActivity");
                    Toast.makeText(MainActivity.this, "No movies found", Toast.LENGTH_SHORT).show();
                    myAdapter = new MyAdapter(null);
                    binding.movieRecyclerView.setAdapter(myAdapter);
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
    }
}