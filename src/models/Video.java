package models;

import java.util.ArrayList;

public class Video implements Comparable<Video> {
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
    protected double rating;
    protected int numRatings;

    public Video(final String title, final int year, final ArrayList<String> cast, final ArrayList<String> genres) {
        this.title = title;
        this.year = year;
        this.cast = cast;
        this.genres = genres;
        this.rating = 0;
        this.numRatings = 0;
    }

    public void addRating(double rate, int season) {

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

    public int getNumRatings(){
        return numRatings;
    }

    @Override
    public int compareTo(Video that) {
        if (Double.compare(this.getRating(), that.getRating()) == 0) {
            return this.getTitle().compareTo(that.getTitle());
        } else {
            return Double.compare(this.getRating(), that.getRating());
        }
    }
}
