package com.example.android.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    private MovieAdapter mAdapter;
    private RecyclerView mMoviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesList = (RecyclerView) findViewById(R.id.rv_movies);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mMoviesList.setLayoutManager(layoutManager);
        mMoviesList.setHasFixedSize(true);
        mAdapter = new MovieAdapter(this);
        mMoviesList.setAdapter(mAdapter);

        loadMoviesData();
    }

    private void loadMoviesData() {
        //showMovieDataView();
        new MovieQueryTask().execute();
    }

    @Override
    public void onClick(String movieClicked) {
        Context context = this;
        Toast.makeText(context, movieClicked, Toast.LENGTH_SHORT).show();
    }

    private void showMovieDataView() {
        mMoviesList.setVisibility(View.VISIBLE);
    }


    private class MovieQueryTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            List<Movie> result = NetworkUtils.fetchMoviesData();
            return result;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if(!movies.isEmpty()){
                showMovieDataView();
                mAdapter.setMovieTitles(movies);
            }

        }
    }


}