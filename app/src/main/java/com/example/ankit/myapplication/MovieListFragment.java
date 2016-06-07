package com.example.ankit.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ankit.myapplication.FetchMoviesTask.CompletedTask;
import com.example.ankit.myapplication.data_object.MovieData;
import com.example.ankit.myapplication.data_object.ReviewData;
import com.example.ankit.myapplication.data_object.TrailerData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ankit on 5/10/2016.
 */

public class MovieListFragment extends Fragment {

    private final String LOG_TAG = MovieListFragment.class.getSimpleName();
    GridView gridView;
    RecyclerView mRecyclerView;
    RequestQueue requestQueue;
    ArrayList<MovieData> mRecyclerMovieDatas;
    private MovieAdapter myAdapter = new MovieAdapter(getActivity(), null);
    ArrayList<MovieData> movieDetails = new ArrayList<MovieData>();
    ArrayList<MovieData> favoritesList = new ArrayList<MovieData>();
    ArrayList<ReviewData> reviewDetails = new ArrayList<ReviewData>();
    ArrayList<TrailerData> trailerDetails = new ArrayList<TrailerData>();
    final String popularMovieParam = "popularity.desc";
    final String highestRated = "vote_average.desc";
    private String posterPath;
    private String requestURL;
    private ListFragmentCallbackInterface callbackListener;
    private boolean twoPane;
    MovieData movieSelected;
    boolean isFavoritesView;
    Context mContext;


    public static MovieListFragment newInstance(boolean twoPane) {
        MovieListFragment listFragment = new MovieListFragment();

        Bundle args = new Bundle();
        args.putBoolean("twoPane", twoPane);
        listFragment.setArguments(args);

        return listFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            twoPane = getArguments().getBoolean("twoPane");
        } else {
            twoPane = savedInstanceState.getBoolean("twoPane");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        mContext = getActivity();
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        /*mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));*/
        requestURL = getString(R.string.movie_main_url) + getString(R.string.api_key);
        startAsyncTask(requestURL);
        //getPopularMovies(mContext, popularMovieParam);
        //gridView.setAdapter((ListAdapter) new PosterAdapter(mContext,mRecyclerMovieDatas));
        //getPopularMovies(mContext, popularMovieParam);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                reviewDetails.clear();
                trailerDetails.clear();
                if (isFavoritesView) {
                    movieSelected = favoritesList.get(position);
                } else {
                    movieSelected = movieDetails.get(position);
                }
                Toast.makeText(getActivity(), " " + movieSelected.getOriginal_title(), Toast.LENGTH_SHORT).show();
                String movieId = movieSelected.getId();
                String trailerReviewRequestUrl = getString(R.string.trailer_base_url) + movieId + "?" +
                        getString(R.string.api_key) + getString(R.string.trailer_review_append);
                findTrailersReviews(trailerReviewRequestUrl);
            }
        });

        return rootView;
    }

    //Request for all movies
    public void startAsyncTask(String requestURL) {
        FetchMoviesTask task = new FetchMoviesTask(new CompletedTask() {
            @Override
            //public void completedTask(ArrayList<MovieData> result, ArrayList<MovieData> details) {
            public void completedTask(String movies) {
                try {
                    ArrayList<MovieData> result = getMovieDataFromJson(movies);
                    myAdapter = new MovieAdapter(getActivity(), result);

                    gridView.setAdapter(myAdapter);

                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        });
        task.execute(requestURL);
    }

    //Request for trailers and reviews of a movie
    public void findTrailersReviews(String requestURL) {
        FetchMoviesTask task = new FetchMoviesTask(new CompletedTask() {
            @Override
            public void completedTask(String movieResult) {
                try {
                    getMovieTrailerReviewsFromJson(movieResult);

                    if (twoPane) {
                        //if in tablet, update fragment
                        callbackListener.updateMovie(movieSelected, isFavoritesView);
                    } else {
                        //start activity
                        callbackListener.startDetailActivity(movieSelected);
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        });
        task.execute(requestURL);
    }

    /**
     * Take the String representing the result of movies in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     */
    private ArrayList<MovieData> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String results = "results";
        final String MOVIEID = "id";
        final String ORIGINAL_TITLE = "original_title";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String POSTER_PATH = "poster_path";
        final String VOTE_AVERAGE = "vote_average";


        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(results);

        for (int i = 0; i < movieArray.length(); i++) {
            MovieData detail = new MovieData();

            // Set details for each movie and add to arraylist
            JSONObject movie = movieArray.getJSONObject(i);
            detail.setId(movie.getString(MOVIEID));
            detail.setOriginal_title(movie.getString(ORIGINAL_TITLE));
            detail.setOverview(movie.getString(OVERVIEW));
            detail.setRelease_date(movie.getString(RELEASE_DATE));
            // Get the poster url and construct it
            String poster = movie.getString(POSTER_PATH);
            detail.setPoster_path(constructPosterURL(poster));
            detail.setVote_average(movie.getString(VOTE_AVERAGE));

            movieDetails.add(detail);
        }
        return movieDetails;
    }

    public void getPopularMovies(final Context context, String param1) {
        requestQueue = Volley.newRequestQueue(context);
        mRecyclerMovieDatas = new ArrayList<>();
        String baseurl = "http://api.themoviedb.org/3/discover/movie?sort_by=" + param1 + "&"
                + "&api_key=" + new KEY().getkey();
        final String baseimgurl = "http://image.tmdb.org/t/p/w342/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(baseurl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arrayItems = response.getJSONArray("results");
                            JSONObject movieObject;
                            for (int i = 0; i < arrayItems.length(); i++) {
                                movieObject = arrayItems.getJSONObject(i);
                                MovieData movie = new MovieData();
                                int id = movieObject.getInt("id");
                                movie.setId(String.valueOf(id));
                                String title, posterPath, releaseDate, overview, ratings, voteCount;
                                title = movieObject.getString("original_title");
                                movie.setOriginal_title(title);
                                posterPath = baseimgurl + movieObject.getString("poster_path");
                                movie.setPoster_path(posterPath);
                                releaseDate = movieObject.getString("release_date");
                                movie.setRelease_date(releaseDate);
                                overview = movieObject.getString("overview");
                                movie.setOverview(overview);
                                ratings = String.valueOf(movieObject.getDouble("vote_average"));
                                movie.setVote_average(ratings);
                                // movie.voteCount = movieObject.getInt("vote_count");
                                mRecyclerMovieDatas.add(movie);
                            }
                            // Toast.makeText(context, "Data Fetching Successful ", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Toast.makeText(context, "Catch Error " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView.setAdapter(new PosterAdapter(context, mRecyclerMovieDatas, MovieListFragment.this));

                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error Parsing JSON " + error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Take the String representing the result of a movie in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     */
    private void getMovieTrailerReviewsFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String REVIEWS = "reviews";
        final String TRAILERS = "trailers";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray reviews = movieJson.getJSONObject(REVIEWS).getJSONArray("results");
        JSONArray trailers = movieJson.getJSONObject(TRAILERS).getJSONArray("youtube");

        for (int i = 0; i < reviews.length(); i++) {
            ReviewData reviewDetail = new ReviewData();
            // Set details for each review and add to arraylist
            JSONObject review = reviews.getJSONObject(i);
            reviewDetail.setContent(review.getString("content"));
            reviewDetail.setAuthor(review.getString("author"));
            reviewDetails.add(reviewDetail);
        }

        for (int i = 0; i < trailers.length(); i++) {
            TrailerData trailerDetail = new TrailerData();
            // Set details for each trailer and add to arraylist
            JSONObject trailer = trailers.getJSONObject(i);
            trailerDetail.setSource(trailer.getString("source"));
            trailerDetail.setName(trailer.getString("name"));
            trailerDetails.add(trailerDetail);
        }
        //Add reviews and trailers to movieSelected
        movieSelected.setReviews(reviewDetails);
        movieSelected.setTrailers(trailerDetails);
    }

    public void updateList(String requestURL, boolean isFavoritesView) {
        movieDetails.clear();
        reviewDetails.clear();
        trailerDetails.clear();
        this.isFavoritesView = isFavoritesView;
        myAdapter.notifyDataSetChanged();
        startAsyncTask(requestURL);
    }

    //Get favorites and update the view to display them
    public void getFavorites(boolean isFavoritesView) {
        favoritesList.clear();
        this.isFavoritesView = isFavoritesView;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        for (int i = 0; i < preferences.getAll().size(); i++) {
            for (int j = 0; j < movieDetails.size(); j++) {
                MovieData possibleMovie = movieDetails.get(j);

                String movieID = preferences.getString("movie:" + possibleMovie.getOriginal_title(), "");
                if ((movieID != null) && (possibleMovie.getId().equalsIgnoreCase(movieID))) {

                    if (!favoritesList.contains(possibleMovie)) {
                        favoritesList.add(possibleMovie);
                    }
                }
            }
        }
        myAdapter.notifyDataSetChanged();
        myAdapter = new MovieAdapter(getActivity(), favoritesList);
        gridView.setAdapter(myAdapter);
    }

    //This function constructs the poster url and size for requesting in Picasso
    public String constructPosterURL(String poster) {
        String baseURL = getString(R.string.base_poster_url);
        String posterSize = "w185";
        posterPath = baseURL + posterSize + poster;

        return posterPath;
    }

    public interface ListFragmentCallbackInterface {
        void updateMovie(MovieData movieSelected, boolean isFavoritesView);

        void startDetailActivity(MovieData movieSelected);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callbackListener = (ListFragmentCallbackInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement MovieListCallbackInterface");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("twoPane", twoPane);
    }


}
