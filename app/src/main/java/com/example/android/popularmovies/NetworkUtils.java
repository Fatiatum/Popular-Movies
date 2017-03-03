package com.example.android.popularmovies;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving movies data from TMDB.
 */
public final class NetworkUtils {

    final static String TMDB_BASE_URL =
            "https://api.themoviedb.org/3/discover/movie?";

    final static String TMDB_MOVIE_URL =
            "https://api.themoviedb.org/3/movie";

    /*
     * sorted by most popular movies or high rated
     * Default: results are sorted by popular movies if no field is specified.
     */
    final static String PARAM_SORT = "sort_by";
    final static String sortByPop = "popularity.desc";
    final static String sortByRate = "vote_average.desc";
    final static String API_KEY = "api_key";
    final static String api = "";

    /** Tag for the log messages */
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link NetworkUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name NetworkUtils (and an object instance of NetworkUtils is not needed).
     */
    private NetworkUtils() {
    }

    /**
     * Builds the URL used to fetch data from TMDB.
     *
     * @return The URL to use to query the TMDB server.
     */
    public static URL getAllMoviesUrl(boolean sortType) {
        Uri builtUri;


        if(sortType){
            builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                    .appendQueryParameter(PARAM_SORT, sortByRate)
                    .appendQueryParameter(API_KEY, api)
                    .build();
        }
        else{
            builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                    .appendQueryParameter(PARAM_SORT, sortByPop)
                    .appendQueryParameter(API_KEY, api)
                    .build();
        }


        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Builds the URL used to fetch data from TMDB.
     *
     * @return The URL to use to query the TMDB server.
     */
    public static URL getMovieUrl(String movie_id) {
        Uri builtUri;

        builtUri = Uri.parse(TMDB_MOVIE_URL).buildUpon()
                .appendPath(movie_id)
                .appendQueryParameter(API_KEY, api)
                .build();



        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Query the TMDB dataset and return a list of {@link Movie} objects.
     */
    public static List<Movie> fetchMoviesData(boolean sortType) {
        // Create URL object
        URL url = getAllMoviesUrl(sortType);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Movie}s
        List<Movie> movies = extractFromJson(jsonResponse);

        // Return the list of {@link Movie}s
        return movies;
    }

    /**
     * Query the TMDB dataset and return a list of {@link Movie} objects.
     */
    public static Movie fetchMovieData(String movie_id) {
        // Create URL object
        URL url = getMovieUrl(movie_id);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Movie}s
        Movie movie = extractMovieFromJson(jsonResponse);

        // Return the list of {@link Movie}s
        return movie;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movies JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Movie> extractFromJson(String movieJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding movies to
        List<Movie> movies = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(movieJSON);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results.
            JSONArray movieArray = baseJsonResponse.getJSONArray("results");

            // For each movie in the movieArray, create an {@link Movie} object
            for (int i = 0; i < movieArray.length(); i++) {

                // Get a single movie at position i within the list of movies
                JSONObject currentMovie = movieArray.getJSONObject(i);

                // Extract the value for the key called "title"
                String title = currentMovie.getString("title");

                // Extract the value for the key called "poster_path"
                String poster_path = currentMovie.getString("poster_path");

                // Extract the value for the key called "id"
                int movie_id = currentMovie.getInt("id");

                Movie movie = new Movie(movie_id, title);

                movie.setPoster(poster_path);

                /*if(all_data) {

                    // Extract the value for the key called "overview"
                    String overview = currentMovie.getString("overview");

                    // Extract the value for the key called "date"
                    String date = currentMovie.getString("release_date");

                    // Extract the value for the key called "popularity"
                    double popularity = currentMovie.getDouble("popularity");

                    // Extract the value for the key called "vote_count"
                    int vote_count = currentMovie.getInt("vote_count");

                    movie.setOverview(overview);
                    movie.setDate(date);
                    movie.setPopularity(popularity);
                    movie.setVotes(vote_count);
                }*/

                // Add the new {@link Movie} to the list of movies.
                movies.add(movie);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("NetworksUtils", "Problem parsing the movies JSON results", e);
        }

        // Return the list of movies
        return movies;
    }

    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing the given JSON response.
     */
    private static Movie extractMovieFromJson(String movieJSON) {
        Movie movie = new Movie();

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject currentMovie = new JSONObject(movieJSON);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results.
            String title = currentMovie.getString("title");

            // Extract the value for the key called "poster_path"
            String poster_path = currentMovie.getString("poster_path");

            // Extract the value for the key called "id"
            int movie_id = currentMovie.getInt("id");

            // Extract the value for the key called "overview"
            String overview = currentMovie.getString("overview");

            // Extract the value for the key called "date"
            String date = currentMovie.getString("release_date");

            // Extract the value for the key called "popularity"
            double popularity = currentMovie.getDouble("popularity");

            // Extract the value for the key called "vote_count"
            int vote_count = currentMovie.getInt("vote_count");


            movie = new Movie(movie_id, title);
            movie.setPoster(poster_path);
            movie.setOverview(overview);
            movie.setDate(date);
            movie.setPopularity(popularity);
            movie.setVotes(vote_count);


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("NetworksUtils", "Problem parsing the movies JSON results", e);
        }

        // Return the list of movies
        return movie;
    }

}