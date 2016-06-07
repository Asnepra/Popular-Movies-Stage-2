package com.example.ankit.myapplication;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ankit.myapplication.data_object.MovieData;
import com.example.ankit.myapplication.data_object.ReviewData;
import com.example.ankit.myapplication.data_object.TrailerData;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Ankit on 5/10/2016.
 */
public class MovieDetailFragment extends Fragment {

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private DetailFragmentCallbackInterface callbackListener;
    private MovieData movieSelected;
    ArrayList<ReviewData> dataToBeFilled;
    ArrayList<TrailerData> trailerDataArrayList;
    String trailerFinalUrl, key, trailerName, baseurl, trailerImageUrl;
    RecyclerView mCommentRecyclerView;
    RequestQueue requestQueue;
    TextView title;
    ImageView poster, trailerImage;
    TextView release;
    TextView rating, trailerTextView;
    FloatingActionButton favoriteButton;
    TextView overView;
    View rootView;
    LinearLayout trailerLayout;
    TextView trailerSection;
    LinearLayout reviewLayout;
    TextView reviewSection;
    int movie_id;


    public static MovieDetailFragment newInstance(MovieData movieSelected) {
        MovieDetailFragment detailFragment = new MovieDetailFragment();

        Bundle args = new Bundle();
        args.putParcelable("movieSelected", movieSelected);
        detailFragment.setArguments(args);

        return detailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            movieSelected = (MovieData) getArguments().getParcelable("movieSelected");
        }
        if (savedInstanceState != null) {
            movieSelected = (MovieData) savedInstanceState.getParcelable("movieSelected");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        title = (TextView) rootView.findViewById(R.id.title);
        poster = (ImageView) rootView.findViewById(R.id.poster);
        release = (TextView) rootView.findViewById(R.id.release);
        rating = (TextView) rootView.findViewById(R.id.rating);
        overView = (TextView) rootView.findViewById(R.id.overview);
        favoriteButton = (FloatingActionButton) rootView.findViewById(R.id.favorite_button);
        trailerLayout = (LinearLayout) rootView.findViewById(R.id.trailerLayout);
        trailerSection = (TextView) rootView.findViewById(R.id.trailerSection);
        trailerTextView = (TextView) rootView.findViewById(R.id.detail_activity_trailers_name);
        trailerImage = (ImageView) rootView.findViewById(R.id.detail_activity_trailer_image);
        reviewLayout = (LinearLayout) rootView.findViewById(R.id.reviewLayout);
        reviewSection = (TextView) rootView.findViewById(R.id.reviewSection);
        mCommentRecyclerView = (RecyclerView) rootView.findViewById(R.id.commentor_recyclerview);
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rootView.setVisibility(View.INVISIBLE);

        if (movieSelected != null) {
            //Show view and update UI
            updateMovie(movieSelected, false);
        }

        return rootView;
    }

    //Sets the UI
    public void updateMovie(final MovieData movieSelected, final boolean isFavoritesView) {

        this.movieSelected = movieSelected;
        final String movieId = movieSelected.getId();
        movie_id = Integer.parseInt(movieSelected.getId());
        rootView.setVisibility(View.VISIBLE);
        title.setText(movieSelected.getOriginal_title());
        Picasso.with(getActivity()).load(movieSelected.getPoster_path()).into(poster);
        String ratingsValue = movieSelected.getVote_average();
        SimpleDateFormat output = new SimpleDateFormat("dd MMM, yyyy");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        String releasedDate = null;
        try {
            releasedDate = output.format(input.parse(movieSelected.getRelease_date()));
        } catch (Exception e) {
            e.printStackTrace();
            //releasedDate = data.releaseDate;
        }
        release.setText("Release date: \n" + releasedDate);
        rating.setText(ratingsValue + " /10 ");
        overView.setText(movieSelected.getOverview());

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                //if id does not exist, add to preferences (favoriting), if it does remove it (unfavoriting)
                if (preferences.getString("movie:" + movieSelected.getOriginal_title(), "").isEmpty()) {
                    editor.putString("movie:" + movieSelected.getOriginal_title(), movieId);
                    editor.commit();
                } else if (!preferences.getString("movie:" + movieSelected.getOriginal_title(), "").isEmpty()) {
                    editor.remove("movie:" + movieSelected.getOriginal_title());
                    editor.commit();
                    if (isFavoritesView) {
                        //update the view if in favorites view
                        callbackListener.updateFavoritesView();
                    }
                }
                Toast.makeText(getActivity(), "Again Clicking on Favourite Button \n" +
                        "will remove movie from Favourites ", Toast.LENGTH_LONG).show();
                Snackbar.make(rootView, "Movie Added to favourites ", Snackbar.LENGTH_SHORT).show();
            }
        });

        //Check if there are trailers and display them
        if (movieSelected.getTrailers().isEmpty()) {
            trailerSection.setText(getString(R.string.no_trailers_title));

        } else {
            trailerSection.setText(getString(R.string.trailers_title));
            try {
                getTrailer();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Error " + e.toString(), Toast.LENGTH_LONG).show();
            }
            //Dynamically add trailers
            for (int i = 0; i < movieSelected.getTrailers().size(); i++) {
                TrailerData trailer = new TrailerData();
                trailer = movieSelected.getTrailers().get(i);
                final String url = trailer.getSource();
                TextView movieTrailer = new TextView(getActivity());
                movieTrailer.setText(trailer.getName());
                movieTrailer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_arrow_black_18dp, 0, 0, 0);
                movieTrailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openTrailer(url);
                    }
                });
                trailerLayout.addView(movieTrailer);
            }
        }

        //Check if there are reviews and display them
        if (movieSelected.getReviews().isEmpty()) {
            reviewSection.setText(getString(R.string.no_reviews_title));
        } else {
            reviewSection.setText(getString(R.string.reviews_title));
            //Dynamically add reviews
            try {
                getReviews();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Error " + e.toString(), Toast.LENGTH_LONG).show();
            }
            /*//getReviews();
            for (int i = 0; i < movieSelected.getReviews().size(); i++) {
                //getReviews();
                ReviewData review = new ReviewData();
                review = movieSelected.getReviews().get(i);
                TextView movieReview = new TextView(getActivity());
                movieReview.setText(Html.fromHtml(review.getContent() + "<b>" + " -- " + review.getAuthor() + "</b>" + "<br />"));
                movieReview.setPadding(0, 15, 0, 15);

                reviewLayout.addView(movieReview);
            }*/
        }

    }

    public void getReviews() {
        dataToBeFilled = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getActivity());

        String baseurl = "http://api.themoviedb.org/3/movie/" + movie_id + "/reviews"
                + "?api_key=" + new KEY().getkey();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(baseurl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray items = response.getJSONArray("results");
                            JSONObject commentObjectt;
                            for (int i = 0; i < items.length(); i++) {
                                commentObjectt = items.getJSONObject(i);
                                ReviewData comment = new ReviewData();
                                String author = commentObjectt.getString("author");
                                comment.setAuthor(author);
                                String description = commentObjectt.getString("content");
                                comment.setContent(description);
                                dataToBeFilled.add(comment);
                            }

                            Toast.makeText(getActivity(), "Comments Fetch Sucessfull ", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            //Log.e(LOG_TAG, e.toString());
                            Toast.makeText(getActivity(), "Catch Error Comments Fetch  " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCommentRecyclerView.setAdapter(new CommentAdapter(getActivity(), dataToBeFilled));
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d(LOG_TAG, "error parsing JSON");
                Toast.makeText(getActivity(), "Error Parsing JSON Comment", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void getTrailer() {
        trailerDataArrayList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getActivity());
        String baseurl = "http://api.themoviedb.org/3/movie/" + movie_id + "/videos"
                + "?api_key=" + new KEY().getkey();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(baseurl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray items = response.getJSONArray("results");
                            JSONObject movie_trailer_object;
                            for (int i = 0; i < items.length(); i++) {
                                movie_trailer_object = items.getJSONObject(i);
                                TrailerData trailerData = new TrailerData();
                                //trailerData.name = data.title + movie_trailer_object.getString("name");
                                trailerName = movie_trailer_object.getString("name");
                                key = movie_trailer_object.getString("key");
                                trailerFinalUrl = "https://www.youtube.com/watch?v=" + key;
                                trailerImageUrl = "http://img.youtube.com/vi/" + key + "/hqdefault.jpg";
                                trailerData.trailerUrl = trailerFinalUrl;
                                trailerData.imageUrl = trailerImageUrl;
                                //trailerDataToBeFilled.add(trailerData);
                                //siteName = movie_trailer_object.getString("site");
                            }
                            Toast.makeText(getActivity(), "Trailer Fetch Succesful ", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            //Log.e(LOG_TAG, e.toString());
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //siteName = siteName.toLowerCase();
                                //mTrailerRecyclerView.setAdapter(new TrailerAdapter(DetailActivity.this, trailerDataToBeFilled));
                                trailerFinalUrl = "https://www.youtube.com/watch?v=" + key;
                                trailerImageUrl = "http://img.youtube.com/vi/" + key + "/default.jpg";
                                //Toast.makeText(getBaseContext(), "Call", Toast.LENGTH_SHORT).show();
                                //trailerTextView.setText("Trailers info\n" + trailerFinalUrl);
                                //Loading the Thumbnail of trailer
                                trailerTextView.setText(trailerName);
                                Picasso.with(getActivity())
                                        .load(trailerImageUrl)
                                        .placeholder(R.drawable.cute_girl1)
                                        .into(trailerImage);
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d(LOG_TAG, "error parsing JSON");
                Toast.makeText(getActivity(), "Error Parsing JSON trailer", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void clearView() {
        //Clear the views and layouts sort preference is selected
        rootView.setVisibility(View.GONE);
        trailerLayout.removeAllViews();
        reviewLayout.removeAllViews();
    }

    public void openTrailer(String trailerURL) {
        try {
            //Open in browser
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.youtube_base_url) + trailerURL));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "Error launching Trailer " + e.toString(), Toast.LENGTH_SHORT).show();
            //Log.e(LOG_TAG, ex.getMessage(), e);
            //e.printStackTrace();
        }
    }

    public interface DetailFragmentCallbackInterface {
        public void updateFavoritesView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callbackListener = (DetailFragmentCallbackInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement DetailFragmentCallbackInterface");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("movieSelected", movieSelected);
    }
}

