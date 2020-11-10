package models;

import fileio.MovieInputData;

public class Movie extends Video implements Comparable<Movie> {
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

    public void addRating(double rate) {
        this.numRatings++;
        this.rating = this.rating * (this.numRatings - 1) / this.numRatings + rate / this.numRatings;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public int compareTo(Movie that) {
        return Double.compare(this.getRating(), that.rating);
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
