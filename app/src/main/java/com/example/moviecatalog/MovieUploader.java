package com.example.moviecatalog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MovieUploader extends AppCompatActivity {

    StorageReference storageReference;
    FirebaseFirestore db;
    LinearProgressIndicator progressIndicator;
    Uri image;
    ImageView imageView;
    MaterialButton selectImageBtn, uploadImageBtn;

    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                image = result.getData().getData();
                                Glide.with(MovieUploader.this).load(image).into(imageView);
                                uploadImageBtn.setEnabled(true);
                            } else {
                                Toast.makeText(MovieUploader.this, "Please select an image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_uploader); // Ensure this layout exists

        FirebaseApp.initializeApp(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        imageView = findViewById(R.id.imageView);
        progressIndicator = findViewById(R.id.progressIndicator);
        selectImageBtn = findViewById(R.id.selectImageBtn);
        uploadImageBtn = findViewById(R.id.uploadImageBtn);

        uploadImageBtn.setEnabled(false);

        selectImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });

        uploadImageBtn.setOnClickListener(v -> {
            if (image != null) {
                uploadImage(image);
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage(Uri fileUri) {
        String filename = UUID.randomUUID().toString();
        StorageReference imgRef = storageReference.child("movie_posters/" + filename + ".jpg");

        imgRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Toast.makeText(MovieUploader.this, "Upload successful!", Toast.LENGTH_SHORT).show();
                    saveMovieToFirestore(uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(MovieUploader.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnProgressListener(taskSnapshot -> {
                    long progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressIndicator.setProgress((int) progress);
                });
    }

    private void saveMovieToFirestore(String imageUrl) {
        // Replace with input values if using EditText
        Map<String, Object> movie = new HashMap<>();
        movie.put("title", "The Matrix");
        movie.put("director", "Lana Wachowski, Lilly Wachowski");
        movie.put("category", "Sci-Fi");
        movie.put("releaseDate", "1999");
        movie.put("ratings", "8.7");
        movie.put("castAndCrew", "Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss");
        movie.put("reviews", "Groundbreaking sci-fi with epic action.");
        movie.put("imageUrl", imageUrl);

        db.collection("movies").document("The Matrix").set(movie)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Movie uploaded to Firestore!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save movie: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
