package com.example.moviecatalog;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AdminUploadActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Firestore and Storage
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Upload movie details
        uploadMovieWithImage();
    }

    void uploadMovieWithImage() {
        // Movie details
        String movieTitle = "Inception";
        String director = "Christopher Nolan";
        String category = "Sci-Fi";
        String releaseDate = "2010";
        String ratings = "8.8";
        String castAndCrew = "Leonardo DiCaprio, Joseph Gordon-Levitt, Ellen Page";
        String reviews = "A mind-bending thriller with stunning visuals.";

        // Movie poster image (Local File Path in res/drawable)
        Uri imageUri = Uri.parse("android.resource://" + getPackageName() + "/drawable/inception_poster");

        // Check if movie already exists in Firestore
        db.collection("movies").document(movieTitle).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Log.d("Firestore", "Movie already exists! Skipping upload.");
                            Toast.makeText(AdminUploadActivity.this, "Movie already exists!", Toast.LENGTH_SHORT).show();
                        } else {
                            // If movie does not exist, upload image first
                            uploadImageAndSaveMovie(movieTitle, imageUri, director, category, releaseDate, ratings, castAndCrew, reviews);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error checking movie existence: " + e.getMessage());
                    Toast.makeText(AdminUploadActivity.this, "Error checking movie!", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImageAndSaveMovie(String movieTitle, Uri imageUri, String director, String category,
                                         String releaseDate, String ratings, String castAndCrew, String reviews) {
        // Reference to store image
        StorageReference imageRef = storageRef.child("movie_posters/" + movieTitle + ".jpg");

        // Upload image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the image URL after upload
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Now upload movie details with image URL
                        saveMovieDetails(movieTitle, director, category, releaseDate, ratings, castAndCrew, reviews, uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase Storage", "Image upload failed: " + e.getMessage());
                    Toast.makeText(AdminUploadActivity.this, "Image upload failed!", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveMovieDetails(String title, String director, String category, String releaseDate, String ratings,
                                  String castAndCrew, String reviews, String imageUrl) {
        // Movie data
        Map<String, Object> movie = new HashMap<>();
        movie.put("title", title);
        movie.put("director", director);
        movie.put("category", category);
        movie.put("releaseDate", releaseDate);
        movie.put("ratings", ratings);
        movie.put("castAndCrew", castAndCrew);
        movie.put("reviews", reviews);
        movie.put("imageUrl", imageUrl);

        // Upload movie details to Firestore
        db.collection("movies").document(title).set(movie)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Movie uploaded successfully!");
                    Toast.makeText(AdminUploadActivity.this, "Movie uploaded!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Movie upload failed: " + e.getMessage());
                    Toast.makeText(AdminUploadActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                });
    }
}
