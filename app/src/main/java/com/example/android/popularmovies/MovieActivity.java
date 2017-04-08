package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieActivity extends AppCompatActivity implements TrailerAdapter.ListItemClickListener{

    private TrailerAdapter mAdapter;
    private RecyclerView mTrailersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_layout);

        mTrailersList = (RecyclerView) findViewById(R.id.rv_trailers);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mTrailersList.setLayoutManager(layoutManager);
        mTrailersList.setHasFixedSize(true);
        mAdapter = new TrailerAdapter(this);
        mTrailersList.setAdapter(mAdapter);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra("movie_id")) {
            String movie_id = intentThatStartedThisActivity.getStringExtra("movie_id");
            new MovieQueryTask().execute(movie_id);
        }
    }

    @Override
    public void onClick(String trailerClicked) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/" + trailerClicked)));
    }

    private class MovieQueryTask extends AsyncTask<String, Void, Movie> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Movie doInBackground(String... params) {
            Movie result = NetworkUtils.fetchMovieData(params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(Movie movie) {
            TextView movieTitle = (TextView) findViewById(R.id.movie_title);
            movieTitle.setText(movie.getTitle());

            TextView movieDate = (TextView) findViewById(R.id.movie_date);
            movieDate.setText(movie.getDate());

            TextView movieSynopsis = (TextView) findViewById(R.id.movie_synopsis);
            movieSynopsis.setText(movie.getOverview());

            TextView movieVotes = (TextView) findViewById(R.id.movie_voting);
            movieVotes.setText(movie.getVotes());

            mTrailersList.setVisibility(View.VISIBLE);
            mAdapter.setTrailers(movie.getTrailers());

            ImageView moviePoster = (ImageView) findViewById(R.id.movie_poster);
            Picasso.with(moviePoster.getContext()).load(movie.getPoster()).resize(400,600).into(moviePoster);

        }
    }
}