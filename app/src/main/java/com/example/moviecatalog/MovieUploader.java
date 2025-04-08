package com.example.moviecatalog;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MovieUploader extends AppCompatActivity {

    private FirebaseFirestore db;

    private EditText titleEditText, directorEditText, categoryEditText,
            releaseDateEditText, ratingsEditText, castEditText, reviewsEditText;
    private MaterialButton uploadDataBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_uploader);

        db = FirebaseFirestore.getInstance();

        titleEditText = findViewById(R.id.titleEditText);
        directorEditText = findViewById(R.id.directorEditText);
        categoryEditText = findViewById(R.id.categoryEditText);
        releaseDateEditText = findViewById(R.id.releaseDateEditText);
        ratingsEditText = findViewById(R.id.ratingsEditText);
        castEditText = findViewById(R.id.castEditText);
        reviewsEditText = findViewById(R.id.reviewsEditText);
        uploadDataBtn = findViewById(R.id.uploadDataBtn);

        uploadDataBtn.setOnClickListener(v -> uploadMovieToFirestore());
    }

    private void uploadMovieToFirestore() {
        String title = titleEditText.getText().toString().trim();
        String director = directorEditText.getText().toString().trim();
        String category = categoryEditText.getText().toString().trim();
        String releaseDate = releaseDateEditText.getText().toString().trim();
        String ratings = ratingsEditText.getText().toString().trim();
        String castAndCrew = castEditText.getText().toString().trim();
        String reviews = reviewsEditText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter the movie title", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> movie = new HashMap<>();
        movie.put("title", title);
        movie.put("director", director);
        movie.put("category", category);
        movie.put("releaseDate", releaseDate);
        movie.put("ratings", ratings);
        movie.put("castAndCrew", castAndCrew);
        movie.put("reviews", reviews);
        movie.put("imageUrl", ""); // Placeholder

        db.collection("movies").document(title).set(movie)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "✅ Movie uploaded to Firestore!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "❌ Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
