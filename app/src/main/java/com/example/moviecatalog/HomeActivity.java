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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recentMoviesRecyclerView;
    private RecyclerView categoriesRecyclerView;
    private EditText searchBar;
    private List<Movie> allMovies; // List to hold all movies for search functionality

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recentMoviesRecyclerView = findViewById(R.id.recent_movies_recycler_view);
        categoriesRecyclerView = findViewById(R.id.categories_recycler_view);
        searchBar = findViewById(R.id.search_bar);

        // Set up RecyclerViews
        setupRecentMoviesRecyclerView();
        setupCategoriesRecyclerView();

        // Fetch data from Firestore
        fetchMovies();
        fetchCategories();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentMoviesRecyclerView.setLayoutManager(layoutManager);
        // Set up search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMovies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRecentMoviesRecyclerView() {
        recentMoviesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupCategoriesRecyclerView() {
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void fetchMovies() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("movies")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allMovies = new ArrayList<>(); // Initialize the list
                        List<Movie> movies = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Movie movie = document.toObject(Movie.class);
                            movies.add(movie);
                            allMovies.add(movie); // Add to allMovies for search
                        }
                        recentMoviesRecyclerView.setAdapter(new RecentMoviesAdapter(movies, this));
                    } else {
                        Log.w("HomeActivity", "Error getting documents.", task.getException());
                    }
                });
    }

    private void fetchCategories() {
        // Assuming you have a predefined list of categories
        List<String> categories = Arrays.asList("Love", "Horror", "Action", "Comedy", "Drama");
        categoriesRecyclerView.setAdapter(new CategoriesAdapter(categories, this));
    }

    private void filterMovies(String query) {
        List<Movie> filteredMovies = new ArrayList<>();
        for (Movie movie : allMovies) { // Use the allMovies list for filtering
            if (movie.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredMovies.add(movie);
            }
        }
        recentMoviesRecyclerView.setAdapter(new RecentMoviesAdapter(filteredMovies, this));
    }

        public class RecentMoviesAdapter extends RecyclerView.Adapter<RecentMoviesAdapter.MovieViewHolder> {
            private List<Movie> recentMovies;
            private Context context;

            public RecentMoviesAdapter(List<Movie> recentMovies, Context context) {
                this.recentMovies = recentMovies;
                this.context = context;
            }

            @NonNull
            @Override
            public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
                return new MovieViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
                Movie movie = recentMovies.get(position);
                holder.movieTitle.setText(movie.getTitle());
                holder.movieCategory.setText(movie.getCategory());

                // Handle movie item clicks
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, MovieDetailActivity.class);
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
                return recentMovies.size();
            }

            // ✅ Remove 'static' from MovieViewHolder
            public class MovieViewHolder extends RecyclerView.ViewHolder {
                TextView movieTitle;
                TextView movieCategory;

                public MovieViewHolder(@NonNull View itemView) {
                    super(itemView);
                    movieTitle = itemView.findViewById(R.id.movie_title);
                    movieCategory = itemView.findViewById(R.id.movie_category);
                }
            }
        }

}