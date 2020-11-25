package models.video;

import entertainment.Season;
import fileio.SerialInputData;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for Show. Extends Video class
 */
public final class Show extends Video {
    private final int numberOfSeasons;
    private final ArrayList<ShowSeason> seasons;

    public Show(final SerialInputData showData) {
        super(showData.getTitle(), showData.getYear(), showData.getCast(), showData.getGenres());
        this.numberOfSeasons = showData.getNumberSeason();
        this.seasons = new ArrayList<>();
        for (Season season : showData.getSeasons()) {
            this.seasons.add(new ShowSeason(season));
        }
    }

    public int getNumberSeason() {
        return numberOfSeasons;
    }

    public ArrayList<ShowSeason> getSeasons() {
        return seasons;
    }

    /**
     * adds rating to a season
     * @param rate rating to be added
     * @param seasonNum number of season rated
     */
    public void addRating(final double rate, final int seasonNum) {
        this.getSeasons().get(seasonNum - 1).addRating(rate);
    }

    /**
     * Gets the rating of the shows.
     * Calculated as the mean of every season's average rating
     * @return rating of show
     */
    @Override
    public double getRating() {
        double sum = 0;
        for (ShowSeason season : this.getSeasons()) {
            List<Double> doubleArrayList = season.getRatings();
            double seasonMean = 0;
            for (Double rating : doubleArrayList) {
                seasonMean += rating;
            }

            if (seasonMean != 0) {
                seasonMean /= doubleArrayList.size();
            }
            sum += seasonMean;
        }

        if (sum == 0) {
            return 0;
        }
        sum /= this.getSeasons().size();
        return sum;
    }

    /**
     * Gets the duration of the Show.
     * Calculated as the sum of every season duration
     * @return duration
     */
    @Override
    public int getDuration() {
        int duration = 0;
        for (ShowSeason season : this.getSeasons()) {
            duration += season.getDuration();
        }
        return duration;
    }

    @Override
    public String toString() {
        return "SerialInputData{" + " title= "
                + super.getTitle() + " " + " year= "
                + super.getYear() + " cast {"
                + super.getCast() + " }\n" + " genres {"
                + super.getGenres() + " }\n "
                + " numberSeason= " + numberOfSeasons
                + ", seasons=" + seasons + "\n\n" + '}';
    }
}
