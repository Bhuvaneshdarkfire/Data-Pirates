package com.example.moviecatalog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AdminUploadActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private StorageReference storageRef;

    private static final String TAG = "AdminUploadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_upload);

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Upload a single movie
        uploadMovieIfNotExists(
                "Inception",
                "Christopher Nolan",
                "Sci-Fi",
                "2010",
                "8.8",
                "Leonardo DiCaprio, Joseph Gordon-Levitt, Ellen Page",
                "A mind-bending thriller with stunning visuals.",
                R.drawable.inception
        );
    }

    private void uploadMovieIfNotExists(String title, String director, String category,
                                        String releaseDate, String ratings, String castAndCrew,
                                        String reviews, int imageResId) {

        firestore.collection("movies").document(title).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Toast.makeText(this, "Movie already exists!", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadPosterImage(title, imageResId, imageUrl ->
                                uploadMovieToFirestore(title, director, category, releaseDate,
                                        ratings, castAndCrew, reviews, imageUrl));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking movie existence", e);
                    Toast.makeText(this, "Failed to check movie!", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadPosterImage(String movieTitle, int imageResId, OnImageUploadComplete callback) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResId);

        if (bitmap == null) {
            Toast.makeText(this, "Failed to decode image!", Toast.LENGTH_SHORT).show();
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference imageRef = storageRef.child("movie_posters/" + movieTitle + ".jpg");

        imageRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            Log.d(TAG, "Image uploaded: " + uri.toString());
                            callback.onComplete(uri.toString());
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to get download URL", e);
                            Toast.makeText(this, "Image URL fetch failed!", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Image upload failed", e);
                    Toast.makeText(this, "Image upload failed!", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadMovieToFirestore(String title, String director, String category,
                                        String releaseDate, String ratings, String castAndCrew,
                                        String reviews, String imageUrl) {

        Map<String, Object> movie = new HashMap<>();
        movie.put("title", title);
        movie.put("director", director);
        movie.put("category", category);
        movie.put("releaseDate", releaseDate);
        movie.put("ratings", ratings);
        movie.put("castAndCrew", castAndCrew);
        movie.put("reviews", reviews);
        movie.put("imageUrl", imageUrl);

        firestore.collection("movies").document(title).set(movie)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Movie uploaded successfully!");
                    Toast.makeText(this, "Movie uploaded to Firestore!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to upload movie", e);
                    Toast.makeText(this, "Upload failed!", Toast.LENGTH_SHORT).show();
                });
    }

    interface OnImageUploadComplete {
        void onComplete(String imageUrl);
    }
}
