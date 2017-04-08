package com.example.android.popularmovies;

import java.util.List;

/**
 * Created by fc__j on 22/02/2017.
 */
public class Movie {

    private static final String IMAGE_PATH = "http://image.tmdb.org/t/p/w185";

    private int mId;

    /** Title of the movie */
    private String mTitle;

    /** Overview of the movie */
    private String mOverview;

    /** Poster Path of the movie */
    private String mPoster;

    /** Release Date of the movie */
    private String mDate;

    /** Popularity of the movie */
    private double mPopularity;

    /** Votes Count of the movie */
    private int mVotes;

    private List<String> trailers;

    public Movie(int id, String mTitle) {
        this.mTitle = mTitle;
        this.mId = id;
    }

    public Movie(){}

    public void setOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public void setPopularity(double mPopularity) {
        this.mPopularity = mPopularity;
    }

    public void setPoster(String mPoster) {
        this.mPoster = mPoster;
    }

    public void setVotes(int mVotes) {
        this.mVotes = mVotes;
    }

    public void setTrailers(List<String> trailers) {
        this.trailers = trailers;
    }

    public int getId() {
        return mId;
    }

    public String getDate() {
        return mDate;
    }

    public String getOverview() {
        return mOverview;
    }

    public double getPopularity() {
        return mPopularity;
    }

    public String getPoster() {
        return IMAGE_PATH + mPoster;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getVotes() {
        return mVotes + "";
    }

    public List<String> getTrailers() {
        return trailers;
    }
}
