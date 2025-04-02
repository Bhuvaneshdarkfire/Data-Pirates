package com.example.moviecatalog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MovieDetailActivity extends AppCompatActivity {
    private TextView movieTitle, movieDirector, movieCategory, movieReleaseDate, movieRatings, movieCastAndCrew, movieReviews;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movieTitle = findViewById(R.id.movie_title);
        movieDirector = findViewById(R.id.movie_director);
        movieCategory = findViewById(R.id.movie_category);
        movieReleaseDate = findViewById(R.id.movie_release_date);
        movieRatings = findViewById(R.id.movie_ratings);
        movieCastAndCrew = findViewById(R.id.movie_cast_and_crew);
        movieReviews = findViewById(R.id.movie_reviews);
        backButton = findViewById(R.id.back_button);

        // Get the movie data from the Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String director = intent.getStringExtra("director");
        String category = intent.getStringExtra("category");
        String releaseDate = intent.getStringExtra("releaseDate");
        String ratings = intent.getStringExtra("ratings");
        String castAndCrew = intent.getStringExtra("castAndCrew");
        String reviews = intent.getStringExtra("reviews");

        // Set the movie details to the TextViews
        movieTitle.setText(title);
        movieDirector.setText("Director: " + director);
        movieCategory.setText("Category: " + category);
        movieReleaseDate.setText("Release Date: " + releaseDate);
        movieRatings.setText("Ratings: " + ratings);
        movieCastAndCrew.setText("Cast & Crew: " + castAndCrew);
        movieReviews.setText("Reviews: " + reviews);

        // Set back button listener
        backButton.setOnClickListener(v -> finish());
    }
}