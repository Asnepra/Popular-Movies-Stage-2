package com.example.ankit.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ankit.myapplication.data_object.MovieData;
import com.example.ankit.myapplication.data_object.ReviewData;
import com.example.ankit.myapplication.data_object.TrailerData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Ankit on 4/21/2016.
 */
public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.ViewHolder> {
    Context mContext;
    ArrayList<MovieData> movieData;
    ArrayList<MovieData> favoritesList = new ArrayList<MovieData>();
    ArrayList<ReviewData> reviewDetails = new ArrayList<ReviewData>();
    ArrayList<TrailerData> trailerDetails = new ArrayList<TrailerData>();
    Fragment mFragment;
    MovieListFragment movieListFragment;
    Bundle mBundle;
    MovieData movieSelected;
    boolean isFavoritesView;

    public PosterAdapter(Context context, ArrayList<MovieData> movieData, MovieListFragment movieListFragment) {
        this.mContext = context;
        this.movieData = movieData;
        this.movieListFragment = movieListFragment;
    }

    public PosterAdapter(Context mContext, ArrayList<MovieData> mRecyclerMovieDatas) {
        this.mContext = mContext;
        this.movieData = mRecyclerMovieDatas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_movie, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView title;
        ImageView poster;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardView);
            title = (TextView) view.findViewById(R.id.deatil_activity_movie_title);
            poster = (ImageView) view.findViewById(R.id.movie_poster);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MovieData item = movieData.get(position);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewDetails.clear();
                trailerDetails.clear();
                if (isFavoritesView) {
                    movieSelected = favoritesList.get(position);
                } else {
                    movieSelected = movieData.get(position);
                }
                Toast.makeText(mContext, " " + movieSelected.getOriginal_title(), Toast.LENGTH_SHORT).show();
                String movieId = movieSelected.getId();
                //String trailerReviewRequestUrl = getString(R.string.trailer_base_url) + movieId + "?" +
                //getString(R.string.api_key) + getString(R.string.trailer_review_append);
                String trailerUrl = "http://api.themoviedb.org/3/movie/" + movieId + "?api_key=" +
                        new KEY().getkey() + "&append_to_response=reviews,trailers";
                movieListFragment.findTrailersReviews(trailerUrl);
                //movieListFragment.findTrailersReviews(trailerReviewRequestUrl);
                /*Toast.makeText(mContext, movieData.get(position).getOriginal_title(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext, MovieDetailActivity.class);
                intent.putExtra("MovieData", (Parcelable) movieData.get(position));
                ActivityOptions activityOptions = ActivityOptions.makeScaleUpAnimation(view, 0, 0,
                        view.getWidth(), view.getHeight());

                mContext.startActivity(intent, activityOptions.toBundle());*/
                //fragmentJump(item);
            }
        });
        Picasso.with(holder.poster.getContext())
                .load(movieData.get(position).getPoster_path())
                .placeholder(R.drawable.cute_girl1)
                .into(holder.poster);
        holder.title.setText(movieData.get(position).getOriginal_title());
    }

  /*  private void fragmentJump(MovieData item) {
        mFragment = new MovieDetailFragment();
        mBundle = new Bundle();
        mBundle.putParcelable("item_selected_key", item);
        mFragment.setArguments(mBundle);
        switchContent(R.id.fragment_detail, mFragment);
    }
    public void switchContent(int id, Fragment fragment) {
        if (mContext == null)
            return;
        if (mContext instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) mContext;
            Fragment frag = fragment;
            mainActivity.switchContent(id, frag);
        }

    }*/

    @Override
    public int getItemCount() {
        return (null != movieData ? movieData.size() : 0);
    }
}
