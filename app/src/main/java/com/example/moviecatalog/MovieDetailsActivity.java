package com.example.moviecatalog;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MovieDetailsActivity extends BaseActivity {

    private ImageView movieImage;
    private TextView titleText, directorText, categoryText, releaseDateText, ratingsText, castText, reviewsText;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityLayout(R.layout.activity_movie_detail); // 👈 inject your layout

        db = FirebaseFirestore.getInstance();

        // Initialize views
        movieImage = findViewById(R.id.movieImage);
        titleText = findViewById(R.id.titleText);
        directorText = findViewById(R.id.directorText);
        categoryText = findViewById(R.id.categoryText);
        releaseDateText = findViewById(R.id.releaseDateText);
        ratingsText = findViewById(R.id.ratingsText);
        castText = findViewById(R.id.castText);
        reviewsText = findViewById(R.id.reviewsText);

        // Load movie details
        String movieTitle = getIntent().getStringExtra("title");
        if (movieTitle != null) {
            fetchMovieDetails(movieTitle);
        } else {
            Toast.makeText(this, "No movie title provided", Toast.LENGTH_SHORT).show();
        }

        // Navigation listener already set in BaseActivity
    }

    private void fetchMovieDetails(String title) {
        db.collection("movies").document(title).get()
                .addOnSuccessListener(this::displayMovieData)
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

        String rawImagePath = doc.getString("imagePath");
        if (rawImagePath != null && !rawImagePath.isEmpty()) {
            String formattedUrl = getFormattedImageUrl(rawImagePath);
            Glide.with(this)
                    .load(formattedUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.image_load_error)
                    .into(movieImage);
        } else {
            movieImage.setImageResource(R.drawable.image_load_error);
        }
    }

    private String getFormattedImageUrl(String originalUrl) {
        if (originalUrl != null && originalUrl.contains("drive.google.com")) {
            try {
                if (originalUrl.contains("/d/")) {
                    String fileId = originalUrl.split("/d/")[1].split("/")[0];
                    return "https://drive.google.com/uc?export=download&id=" + fileId;
                } else if (originalUrl.contains("id=")) {
                    String fileId = originalUrl.split("id=")[1];
                    return "https://drive.google.com/uc?export=download&id=" + fileId;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return originalUrl;
    }

    // No need to override onNavigationItemSelected or onBackPressed here; it's in BaseActivity

}
