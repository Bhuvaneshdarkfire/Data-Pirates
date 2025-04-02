package com.example.moviecatalog;

public class Movie {
    private String title;
    private String director;
    private String category;
    private String releaseDate;
    private String ratings;
    private String castAndCrew;
    private String reviews;

    // Default constructor required for calls to DataSnapshot.getValue(Movie.class)
    public Movie() {}

    public Movie(String title, String director, String category, String releaseDate, String ratings, String castAndCrew, String reviews) {
        this.title = title;
        this.director = director;
        this.category = category;
        this.releaseDate = releaseDate;
        this.ratings = ratings;
        this.castAndCrew = castAndCrew;
        this.reviews = reviews;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDirector() { return director; }
    public String getCategory() { return category; }
    public String getReleaseDate() { return releaseDate; }
    public String getRatings() { return ratings; }
    public String getCastAndCrew() { return castAndCrew; }
    public String getReviews() { return reviews; }
}