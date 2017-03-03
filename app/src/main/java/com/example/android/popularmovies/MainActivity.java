package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    private MovieAdapter mAdapter;
    private RecyclerView mMoviesList;
    private boolean sortType = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesList = (RecyclerView) findViewById(R.id.rv_movies);
        GridLayoutManager layoutManager;
        Display display = ((WindowManager) mMoviesList.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if(display.getRotation()== Surface.ROTATION_0)
            layoutManager = new GridLayoutManager(this, 3);
        else
            layoutManager = new GridLayoutManager(this, 5);
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
        Class movieActivity = MovieActivity.class;
        Intent movieActivityIntent = new Intent(context, movieActivity);
        movieActivityIntent.putExtra("movie_id", movieClicked);
        startActivity(movieActivityIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.rate){
            sortType = true;
        }
        else{
            sortType = false;
        }
        loadMoviesData();

        return super.onOptionsItemSelected(item);
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
            List<Movie> result = NetworkUtils.fetchMoviesData(sortType);
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