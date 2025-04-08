package com.example.moviecatalog;

public class Movie {
    private String title;
    private String director;
    private String category;
    private String releaseDate;
    private String ratings;
    private String castAndCrew;
    private String reviews;
    private String imagePath; // ✅ NEW FIELD

    // Default constructor required for Firestore
    public Movie() {}

    // Constructor with imagePath included
    public Movie(String title, String director, String category, String releaseDate,
                 String ratings, String castAndCrew, String reviews, String imagePath) {
        this.title = title;
        this.director = director;
        this.category = category;
        this.releaseDate = releaseDate;
        this.ratings = ratings;
        this.castAndCrew = castAndCrew;
        this.reviews = reviews;
        this.imagePath = imagePath;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDirector() { return director; }
    public String getCategory() { return category; }
    public String getReleaseDate() { return releaseDate; }
    public String getRatings() { return ratings; }
    public String getCastAndCrew() { return castAndCrew; }
    public String getReviews() { return reviews; }
    public String getImagePath() { return imagePath; } // ✅ GETTER

    // Setters (optional but useful for Firebase and editing)
    public void setTitle(String title) { this.title = title; }
    public void setDirector(String director) { this.director = director; }
    public void setCategory(String category) { this.category = category; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public void setRatings(String ratings) { this.ratings = ratings; }
    public void setCastAndCrew(String castAndCrew) { this.castAndCrew = castAndCrew; }
    public void setReviews(String reviews) { this.reviews = reviews; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; } // ✅ SETTER
}
