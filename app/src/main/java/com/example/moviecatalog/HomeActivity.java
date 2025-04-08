package com.example.moviecatalog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView categoriesRecyclerView;
    private EditText searchBar;
    private Map<String, List<Movie>> categoryMap = new HashMap<>();
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        categoriesRecyclerView = findViewById(R.id.categories_recycler_view);
        searchBar = findViewById(R.id.search_bar);

        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategoryAdapter(this, categoryMap);
        categoriesRecyclerView.setAdapter(categoryAdapter);

        fetchMovies();

        // Search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMovies(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchMovies() {
        FirebaseFirestore.getInstance().collection("movies")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        categoryMap.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Movie movie = doc.toObject(Movie.class);
                            String category = movie.getCategory();

                            if (!categoryMap.containsKey(category)) {
                                categoryMap.put(category, new ArrayList<>());
                            }
                            categoryMap.get(category).add(movie);
                        }
                        categoryAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("HomeActivity", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void filterMovies(String query) {
        FirebaseFirestore.getInstance().collection("movies")
                .whereGreaterThanOrEqualTo("title", query)
                .whereLessThanOrEqualTo("title" + "\uf8ff", query + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, List<Movie>> filteredMap = new HashMap<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Movie movie = doc.toObject(Movie.class);
                            String category = movie.getCategory();

                            if (!filteredMap.containsKey(category)) {
                                filteredMap.put(category, new ArrayList<>());
                            }
                            filteredMap.get(category).add(movie);
                        }

                        categoryMap.clear();
                        categoryMap.putAll(filteredMap);
                        categoryAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Search", "Error fetching search results", task.getException());
                    }
                });
    }

    // Nested Adapter for Horizontal Movies inside Categories
    public static class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
        private final Context context;
        private final List<Movie> movieList;

        public MovieAdapter(Context context, List<Movie> movieList) {
            this.context = context;
            this.movieList = movieList;
        }

        @NonNull
        @Override
        public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
            return new MovieViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
            Movie movie = movieList.get(position);
            holder.movieTitle.setText(movie.getTitle());
            holder.movieCategory.setText(movie.getCategory());
            holder.movieImage.setImageResource(R.drawable.inception); // Make sure placeholder exists


            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra("title", movie.getTitle());
                intent.putExtra("director", movie.getDirector());
                intent.putExtra("category", movie.getCategory());
                intent.putExtra("releaseDate", movie.getReleaseDate());
                intent.putExtra("ratings", movie.getRatings());
                intent.putExtra("castAndCrew", movie.getCastAndCrew());
                intent.putExtra("reviews", movie.getReviews());
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return movieList.size();
        }

        public static class MovieViewHolder extends RecyclerView.ViewHolder {
            TextView movieTitle, movieCategory;
            ImageView movieImage;

            public MovieViewHolder(@NonNull View itemView) {
                super(itemView);
                movieTitle = itemView.findViewById(R.id.movie_title);
                movieCategory = itemView.findViewById(R.id.movie_category);
                movieImage = itemView.findViewById(R.id.movie_image);
            }
        }
    }
}
