package com.example.moviefinder.view;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviefinder.R;
import com.example.moviefinder.model.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private List<Movie> favoriteMovies;
    private OnFavoriteActionListener actionListener;
    private static final String TAG = "FavoritesAdapter";

    public interface OnFavoriteActionListener {
        void onEdit(Movie movie);
        void onDelete(Movie movie);
        void onItemClick(Movie movie);
    }

    public FavoritesAdapter(List<Movie> favoriteMovies, OnFavoriteActionListener listener) {
        this.favoriteMovies = favoriteMovies;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_movie_item, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Movie movie = favoriteMovies.get(position);
        
        holder.titleTextView.setText(movie.getTitle() != null ? movie.getTitle() : "Unknown Title");
        holder.yearTextView.setText(movie.getYear() != null ? movie.getYear() : "Unknown Year");
        holder.studioTextView.setText(movie.getStudio() != null ? movie.getStudio() : "Unknown Studio");
        holder.ratingTextView.setText(movie.getRating() != null ? movie.getRating() : "Not Rated");

        // Load poster image
        String posterUrl = movie.getPosterUrl();
        if (posterUrl != null && !posterUrl.equals("N/A")) {
            if (posterUrl.startsWith("http://")) {
                posterUrl = posterUrl.replace("http://", "https://");
            }
            
            Picasso.get()
                    .load(posterUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.posterImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Successfully loaded image for: " + movie.getTitle());
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "Failed to load image for: " + movie.getTitle() + ", Error: " + e.getMessage());
                        }
                    });
        } else {
            holder.posterImageView.setImageResource(R.drawable.placeholder_image);
        }

        // Set up click listeners
        holder.itemView.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onItemClick(movie);
            }
        });
        
        holder.editButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEdit(movie);
            }
        });
        
        holder.deleteButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDelete(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteMovies != null ? favoriteMovies.size() : 0;
    }

    public void updateData(List<Movie> newMovies) {
        this.favoriteMovies = newMovies;
        notifyDataSetChanged();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImageView;
        TextView titleTextView, yearTextView, studioTextView, ratingTextView;
        ImageButton editButton, deleteButton;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.favoriteMoviePoster);
            titleTextView = itemView.findViewById(R.id.favoriteMovieTitle);
            yearTextView = itemView.findViewById(R.id.favoriteMovieYear);
            studioTextView = itemView.findViewById(R.id.favoriteMovieStudio);
            ratingTextView = itemView.findViewById(R.id.favoriteMovieRating);
            editButton = itemView.findViewById(R.id.editFavoriteButton);
            deleteButton = itemView.findViewById(R.id.deleteFavoriteButton);
        }
    }
} 