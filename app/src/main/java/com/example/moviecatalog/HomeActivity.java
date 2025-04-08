package com.example.moviecatalog;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView categoriesRecyclerView;
    private EditText searchBar;
    private final Map<String, List<Movie>> categoryMap = new HashMap<>();
    private CategoryAdapter categoryAdapter;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Toolbar setup
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Check if your strings.xml contains these:
        // <string name="navigation_drawer_open">Open Navigation</string>
        // <string name="navigation_drawer_close">Close Navigation</string>
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // RecyclerView & SearchBar setup
        categoriesRecyclerView = findViewById(R.id.categories_recycler_view);
        searchBar = findViewById(R.id.search_bar);

        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategoryAdapter(this, categoryMap);
        categoriesRecyclerView.setAdapter(categoryAdapter);

        fetchMovies();

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
                            if (category == null || category.isEmpty()) {
                                category = "Uncategorized";
                            }
                            if (!categoryMap.containsKey(category)) {
                                categoryMap.put(category, new ArrayList<>());
                            }
                            categoryMap.get(category).add(movie);
                        }
                        categoryAdapter.updateCategories(categoryMap);
                    } else {
                        Log.e("HomeActivity", "Error getting documents: ", task.getException());
                        Toast.makeText(this, "Failed to load movies", Toast.LENGTH_SHORT).show();
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
                            if (category == null || category.isEmpty()) {
                                category = "Uncategorized";
                            }
                            filteredMap.computeIfAbsent(category, k -> new ArrayList<>()).add(movie);
                        }
                        categoryMap.clear();
                        categoryMap.putAll(filteredMap);
                        categoryAdapter.updateCategories(categoryMap);
                    } else {
                        Log.e("Search", "Error fetching search results", task.getException());
                        Toast.makeText(this, "Search failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Already in HomeActivity, no need to relaunch
            return true;
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(this, AccountActivity.class));
            return true;
        } else if (id == R.id.nav_wishlist) {
            startActivity(new Intent(this, WishlistActivity.class));
            return true;
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
