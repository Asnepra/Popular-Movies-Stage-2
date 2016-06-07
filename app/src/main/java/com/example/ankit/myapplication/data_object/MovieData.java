package com.example.ankit.myapplication.data_object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class MovieData implements Parcelable {

    private String id;
    private String original_title;
    private String release_date;
    private String vote_average;
    private String poster_path;
    private String overview;
    private ArrayList<ReviewData> reviews = new ArrayList<ReviewData>();
    private ArrayList<TrailerData> trailers = new ArrayList<TrailerData>();

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public ArrayList<ReviewData> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<ReviewData> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<TrailerData> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<TrailerData> trailers) {
        this.trailers = trailers;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(original_title);
        dest.writeString(release_date);
        dest.writeString(vote_average);
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeTypedList(reviews);
        dest.writeTypedList(trailers);

    }

    public MovieData() {

    }

    protected MovieData(Parcel in) {
        id = in.readString();
        original_title = in.readString();
        release_date = in.readString();
        vote_average = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        in.readTypedList(reviews, ReviewData.CREATOR);
        in.readTypedList(trailers, TrailerData.CREATOR);
    }
}
