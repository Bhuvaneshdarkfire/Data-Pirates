package com.example.moviecatalog;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private final Context context;
    private final List<Movie> movieList;
    private static final String TAG = "MovieAdapter";

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

        String rawImagePath = movie.getImagePath();
        Log.d(TAG, "Raw imagePath from Firestore: " + rawImagePath);

        String imageUrl = getFormattedImageUrl(rawImagePath);
        Log.d(TAG, "Formatted image URL: " + imageUrl);

        // Handle null or empty URLs
        if (imageUrl == null || imageUrl.isEmpty()) {
            Log.e(TAG, "Invalid or empty image URL for movie: " + movie.getTitle());
            holder.movieImage.setImageResource(R.drawable.image_load_error);
        } else {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.image_load_error)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "❌ Image load failed for URL: " + imageUrl, e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target,
                                                       com.bumptech.glide.load.DataSource dataSource,
                                                       boolean isFirstResource) {
                            Log.d(TAG, "✅ Image loaded successfully for URL: " + imageUrl);
                            return false;
                        }
                    })
                    .into(holder.movieImage);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailsActivity.class);
            intent.putExtra("title", movie.getTitle());
            intent.putExtra("director", movie.getDirector());
            intent.putExtra("category", movie.getCategory());
            intent.putExtra("releaseDate", movie.getReleaseDate());
            intent.putExtra("ratings", movie.getRatings());
            intent.putExtra("castAndCrew", movie.getCastAndCrew());
            intent.putExtra("reviews", movie.getReviews());
            intent.putExtra("imageUrl", imageUrl);
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

    private String getFormattedImageUrl(String originalUrl) {
        if (originalUrl != null && originalUrl.contains("drive.google.com")) {
            try {
                if (originalUrl.contains("/d/")) {
                    String fileId = originalUrl.split("/d/")[1].split("/")[0];
                    return "https://drive.google.com/uc?export=download&id=" + fileId;
                } else if (originalUrl.contains("id=")) {
                    String fileId = originalUrl.split("id=")[1];
                    return "https://drive.google.com/uc?export=download&id=" + fileId;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error formatting Google Drive URL", e);
            }
        }
        return originalUrl; // fallback (maybe Firebase Storage or other URL)
    }
}
