package models;

import fileio.MovieInputData;

public final class Movie extends Video {
    private final int duration;

    private int numRatings;

    public Movie(final MovieInputData movieData) {
        super(movieData.getTitle(),
              movieData.getYear(),
              movieData.getCast(),
              movieData.getGenres());
        this.duration = movieData.getDuration();
        this.numRatings = 0;
    }

    /**
     *
     * @param rate
     * @param seasonNum
     */
    public void addRating(final double rate, final int seasonNum) {
        this.numRatings++;
        this.rating = this.rating
                * (this.numRatings - 1) / this.numRatings + rate / this.numRatings;
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
