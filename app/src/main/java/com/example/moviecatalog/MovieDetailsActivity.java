package com.example.moviecatalog;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView movieImage;
    private TextView titleText, directorText, categoryText, releaseDateText, ratingsText, castText, reviewsText;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        db = FirebaseFirestore.getInstance();

        movieImage = findViewById(R.id.movieImage);
        titleText = findViewById(R.id.titleText);
        directorText = findViewById(R.id.directorText);
        categoryText = findViewById(R.id.categoryText);
        releaseDateText = findViewById(R.id.releaseDateText);
        ratingsText = findViewById(R.id.ratingsText);
        castText = findViewById(R.id.castText);
        reviewsText = findViewById(R.id.reviewsText);

        // You can pass the movie title via intent
        String movieTitle = getIntent().getStringExtra("title");

        if (movieTitle != null) {
            fetchMovieDetails(movieTitle);
        } else {
            Toast.makeText(this, "No movie title provided", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchMovieDetails(String title) {
        db.collection("movies").document(title).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        displayMovieData(documentSnapshot);
                    } else {
                        Toast.makeText(this, "Movie not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error fetching movie: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void displayMovieData(DocumentSnapshot doc) {
        titleText.setText("Title: " + doc.getString("title"));
        directorText.setText("Director: " + doc.getString("director"));
        categoryText.setText("Category: " + doc.getString("category"));
        releaseDateText.setText("Release Date: " + doc.getString("releaseDate"));
        ratingsText.setText("Ratings: " + doc.getString("ratings"));
        castText.setText("Cast & Crew: " + doc.getString("castAndCrew"));
        reviewsText.setText("Reviews: " + doc.getString("reviews"));

        String imageUrl = doc.getString("imageUrl");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(movieImage);
        }
    }
}
