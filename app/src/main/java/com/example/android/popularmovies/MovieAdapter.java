package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private List<Movie> mMovieTitles;
    private final ListItemClickListener mClickHandler;
    private Context context;

    public interface ListItemClickListener {
        void onClick(String movieClicked);
    }

    public MovieAdapter(ListItemClickListener clickHandler){
        mClickHandler = clickHandler;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView moviePosterView;

        public MovieViewHolder (View itemView){
            super(itemView);
            moviePosterView = (ImageView) itemView.findViewById(R.id.movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie currentMovie = mMovieTitles.get(adapterPosition);
            mClickHandler.onClick(currentMovie.getId() + "");
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movieClicked = mMovieTitles.get(position);
        Picasso.with(context).load(movieClicked.getPoster()).resize(200,300).into(holder.moviePosterView);
    }

    @Override
    public int getItemCount() {
        if (null == mMovieTitles) return 0;
        return mMovieTitles.size();
    }

    public void setMovieTitles(List<Movie> movieTitles) {
        this.mMovieTitles = movieTitles;
        notifyDataSetChanged();
    }
}
