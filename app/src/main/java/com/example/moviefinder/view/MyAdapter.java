package com.example.moviefinder.view;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moviefinder.R;
import com.example.moviefinder.model.Movie;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MovieViewHolder> {

    private List<Movie> movieList;

    public MyAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.titleTextView.setText(movie.getTitle() != null ? movie.getTitle() : "No Title");
        holder.yearTextView.setText(movie.getYear() != null ? movie.getYear() : "No Year");

        String posterUrl = movie.getPosterUrl();
        Log.d("Picasso", "Original poster URL: " + posterUrl);

        if (posterUrl != null && posterUrl.startsWith("http://")) {
            posterUrl = posterUrl.replace("http://", "https://");
            Log.d("Picasso", "Converted to HTTPS: " + posterUrl);
        }

        if (posterUrl != null && !posterUrl.equals("N/A")) {
            Picasso.get()
                    .load(posterUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.posterImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("Picasso", "Successfully loaded image for: " + movie.getTitle());
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("Picasso", "Failed to load image for: " + movie.getTitle() + ", Error: " + e.getMessage());
                        }
                    });
        } else {
            Log.d("Picasso", "Invalid poster URL for: " + movie.getTitle());
            holder.posterImageView.setImageResource(R.drawable.placeholder_image);
        }

        // Add click listener to the item view
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MovieDetailActivity.class);
            intent.putExtra("imdbID", movie.getImdbID());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList != null ? movieList.size() : 0;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, yearTextView;
        ImageView posterImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            yearTextView = itemView.findViewById(R.id.yearTextView);
            posterImageView = itemView.findViewById(R.id.posterImageView);
        }
    }
}