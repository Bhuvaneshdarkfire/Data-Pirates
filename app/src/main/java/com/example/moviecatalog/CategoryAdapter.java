package com.example.moviecatalog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private Map<String, List<Movie>> categoryMap = new HashMap<>();
    private final List<String> categories = new ArrayList<>();

    public CategoryAdapter(Context context, Map<String, List<Movie>> categoryMap) {
        this.context = context;
        if (categoryMap != null) {
            this.categoryMap.putAll(categoryMap);
            this.categories.addAll(categoryMap.keySet());
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_row, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.categoryTitle.setText(category);

        List<Movie> movies = categoryMap.get(category);
        if (movies != null) {
            MovieAdapter adapter = new MovieAdapter(context, movies);
            holder.moviesRecyclerView.setLayoutManager(
                    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.moviesRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     * Updates the adapter with a new category-to-movie mapping.
     */
    public void updateCategories(Map<String, List<Movie>> newCategoryMap) {
        if (newCategoryMap != null) {
            categoryMap.clear();
            categoryMap.putAll(newCategoryMap);
            categories.clear();
            categories.addAll(newCategoryMap.keySet());
            notifyDataSetChanged();
        }
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        RecyclerView moviesRecyclerView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.category_title);
            moviesRecyclerView = itemView.findViewById(R.id.movies_recyclerview);
        }
    }
}
