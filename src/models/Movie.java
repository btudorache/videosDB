package models;

import fileio.MovieInputData;

public class Movie extends Video {
    /**
     * Duration in minutes of a movie
     */
    private final int duration;

    private int numRatings;


    public Movie(MovieInputData movieData) {
        super(movieData.getTitle(), movieData.getYear(), movieData.getCast(), movieData.getGenres());
        this.duration = movieData.getDuration();
        this.numRatings = 0;
    }

    public void addRating(double rate, int seasonNum) {
        this.numRatings++;
        this.rating = this.rating * (this.numRatings - 1) / this.numRatings + rate / this.numRatings;
    }
    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "MovieInputData{" + "title= "
                + super.getTitle() + "year= "
                + super.getYear() + "duration= "
                + duration + "cast {"
                + super.getCast() + " }\n"
                + "genres {" + super.getGenres() + " }\n ";
    }
}
