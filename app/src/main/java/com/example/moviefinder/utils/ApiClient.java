package com.example.moviefinder.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class ApiClient {

    private static OkHttpClient client = new OkHttpClient();

    public static String getMovies(String query, String apiKey) throws IOException {
        // Construct the URL for the API request
        String url = "https://www.omdbapi.com/?s=" + query + "&apikey=" + apiKey;

        // Create the request
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Make the request and get the response
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Return the response body
            return response.body().string();
        }
    }
}

