package models.video;

import entertainment.Season;

import java.util.ArrayList;
import java.util.List;


/**
 * Class modeling season of a show
 */
public final class ShowSeason {
    private final int currentSeason;
    private int duration;
    private List<Double> ratings;

    public ShowSeason(final Season seasonData) {
        this.currentSeason = seasonData.getCurrentSeason();
        this.duration = seasonData.getDuration();
        this.ratings = new ArrayList<>();
    }

    public int getDuration() {
        return duration;
    }

    public List<Double> getRatings() {
        return ratings;
    }

    /**
     * Add rating to this season
     * @param rating rating to be added
     */
    public void addRating(final double rating) {
        this.ratings.add(rating);
    }

    @Override
    public String toString() {
        return "Episode{"
                + "currentSeason="
                + currentSeason
                + ", duration="
                + duration
                + '}';
    }
}
