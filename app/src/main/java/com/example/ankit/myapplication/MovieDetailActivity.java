package com.example.ankit.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.ankit.myapplication.MovieDetailFragment.DetailFragmentCallbackInterface;
import com.example.ankit.myapplication.data_object.MovieData;

/**
 * Created by Ankit on 5/10/2016.
 */
public class MovieDetailActivity extends AppCompatActivity implements DetailFragmentCallbackInterface {

    private MovieDetailFragment detailFragment;
    MovieData movieSelected = new MovieData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        movieSelected = intent.getParcelableExtra("movieSelected");

        setContentView(R.layout.activity_movie_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (detailFragment == null) {
            detailFragment = MovieDetailFragment.newInstance(movieSelected);
        } else {
            detailFragment.updateMovie(movieSelected, false);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.movie_detail_container, detailFragment);
        fragmentTransaction.commit();
    }

    public void updateFavoritesView(){

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

