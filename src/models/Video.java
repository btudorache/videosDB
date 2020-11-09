package models;

import java.util.ArrayList;

public abstract class Video {
    /**
     * Video's title
     */
    private String title;
    /**
     * The year the video was released
     */
    private int year;
    /**
     * Video casting
     */
    private ArrayList<String> cast;
    /**
     * Video genres
     */
    private ArrayList<String> genres;
    /**
     * Video
     */
    private double rating;

    public Video(final String title, final int year, final ArrayList<String> cast, final ArrayList<String> genres) {
        this.title = title;
        this.year = year;
        this.cast = cast;
        this.genres = genres;
        this.rating = 0;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public ArrayList<String> getCast() {
        return cast;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public double getRating() {
        return rating;
    }
}
