package com.example.moviefinder.utils;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiClient {

    private static OkHttpClient client = new OkHttpClient();

    public static String getMovies(String query, String apiKey) throws IOException {
        String url = "https://www.omdbapi.com/?s=" + query + "&apikey=" + apiKey;
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    public static String getMovieDetails(String imdbID, String apiKey) throws IOException {
        String url = "https://www.omdbapi.com/?i=" + imdbID + "&apikey=" + apiKey;
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }
}