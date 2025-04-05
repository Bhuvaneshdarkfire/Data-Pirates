package com.example.moviecatalog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AdminUploadActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_upload); // Use a proper layout

        // Initialize Firebase Firestore and Storage
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Upload movie details
        uploadMovieWithImage();
    }

    private void uploadMovieWithImage() {
        String movieTitle = "Inception";
        String director = "Christopher Nolan";
        String category = "Sci-Fi";
        String releaseDate = "2010";
        String ratings = "8.8";
        String castAndCrew = "Leonardo DiCaprio, Joseph Gordon-Levitt, Ellen Page";
        String reviews = "A mind-bending thriller with stunning visuals.";

        db.collection("movies").document(movieTitle).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Toast.makeText(this, "Movie already exists!", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadImageAndSaveMovie(movieTitle, director, category, releaseDate, ratings, castAndCrew, reviews);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error checking movie existence", e);
                    Toast.makeText(this, "Error checking movie!", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImageAndSaveMovie(String movieTitle, String director, String category,
                                         String releaseDate, String ratings, String castAndCrew, String reviews) {
        StorageReference imageRef = storageRef.child("movie_posters/" + movieTitle + ".jpg");

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.inception);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        imageRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri ->
                        saveMovieDetails(movieTitle, director, category, releaseDate, ratings, castAndCrew, reviews, uri.toString())
                ))
                .addOnFailureListener(e -> {
                    Log.e("Firebase Storage", "Image upload failed", e);
                    Toast.makeText(this, "Image upload failed!", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveMovieDetails(String title, String director, String category, String releaseDate,
                                  String ratings, String castAndCrew, String reviews, String imageUrl) {
        Map<String, Object> movie = new HashMap<>();
        movie.put("title", title);
        movie.put("director", director);
        movie.put("category", category);
        movie.put("releaseDate", releaseDate);
        movie.put("ratings", ratings);
        movie.put("castAndCrew", castAndCrew);
        movie.put("reviews", reviews);
        movie.put("imageUrl", imageUrl);

        db.collection("movies").document(title).set(movie)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Movie uploaded successfully!");
                    Toast.makeText(this, "Movie uploaded!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Upload failed", e);
                    Toast.makeText(this, "Upload failed!", Toast.LENGTH_SHORT).show();
                });
    }
}
