package com.example.moviecatalog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class MovieUploader extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseFirestore db;
    private StorageReference storageRef;
    private Uri selectedImageUri;

    private EditText titleEditText, directorEditText, categoryEditText,
            releaseDateEditText, ratingsEditText, castEditText, reviewsEditText;
    private MaterialButton uploadDataBtn, selectImageBtn;
    private ImageView moviePosterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_uploader);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("movie_posters");

        titleEditText = findViewById(R.id.titleEditText);
        directorEditText = findViewById(R.id.directorEditText);
        categoryEditText = findViewById(R.id.categoryEditText);
        releaseDateEditText = findViewById(R.id.releaseDateEditText);
        ratingsEditText = findViewById(R.id.ratingsEditText);
        castEditText = findViewById(R.id.castEditText);
        reviewsEditText = findViewById(R.id.reviewsEditText);
        uploadDataBtn = findViewById(R.id.uploadDataBtn);
        selectImageBtn = findViewById(R.id.selectImageBtn);
        moviePosterImageView = findViewById(R.id.moviePosterImageView);

        selectImageBtn.setOnClickListener(v -> pickImageFromGallery());
        uploadDataBtn.setOnClickListener(v -> uploadMovieToFirestore());
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            moviePosterImageView.setImageURI(selectedImageUri);
        }
    }

    private void uploadMovieToFirestore() {
        String title = titleEditText.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter the movie title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select a poster image", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference fileRef = storageRef.child(title + "_" + System.currentTimeMillis() + ".jpg");

        fileRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(this, "✅ Image uploaded. Waiting for finalization...", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            saveMovieToFirestore(imageUrl);
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "❌ Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }, 10000); // 10 seconds delay

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "❌ Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveMovieToFirestore(String imageUrl) {
        String title = titleEditText.getText().toString().trim();

        Map<String, Object> movie = new HashMap<>();
        movie.put("title", title);
        movie.put("director", directorEditText.getText().toString().trim());
        movie.put("category", categoryEditText.getText().toString().trim());
        movie.put("releaseDate", releaseDateEditText.getText().toString().trim());
        movie.put("ratings", ratingsEditText.getText().toString().trim());
        movie.put("castAndCrew", castEditText.getText().toString().trim());
        movie.put("reviews", reviewsEditText.getText().toString().trim());
        movie.put("imageUrl", imageUrl);

        db.collection("movies").document(title)
                .set(movie)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "✅ Movie uploaded successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "❌ Firestore upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
