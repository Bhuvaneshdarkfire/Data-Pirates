package com.example.moviecatalog;

/**
 * Model class representing a Movie object for Firestore.
 */
public class Movie {
    private String title;
    private String director;
    private String category;
    private String releaseDate;
    private String ratings;
    private String castAndCrew;
    private String reviews;
    private String imagePath; // Firebase Storage or Drive image URL

    // Required public no-arg constructor for Firestore
    public Movie() {}

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
    public String getTitle() {
        return title != null ? title : "";
    }

    public String getDirector() {
        return director != null ? director : "";
    }

    public String getCategory() {
        return category != null ? category : "";
    }

    public String getReleaseDate() {
        return releaseDate != null ? releaseDate : "";
    }

    public String getRatings() {
        return ratings != null ? ratings : "";
    }

    public String getCastAndCrew() {
        return castAndCrew != null ? castAndCrew : "";
    }

    public String getReviews() {
        return reviews != null ? reviews : "";
    }

    public String getImagePath() {
        return imagePath != null ? imagePath : "";
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public void setCastAndCrew(String castAndCrew) {
        this.castAndCrew = castAndCrew;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
