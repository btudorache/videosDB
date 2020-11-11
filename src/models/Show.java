package models;

import entertainment.Season;
import fileio.SerialInputData;

import java.util.ArrayList;
import java.util.List;

public class Show extends Video  {
    /**
     * Number of seasons
     */
    private final int numberOfSeasons;
    /**
     * Season list
     */
    private final ArrayList<Season> seasons;


    public Show(SerialInputData showData) {
        super(showData.getTitle(), showData.getYear(), showData.getCast(), showData.getGenres());
        this.numberOfSeasons = showData.getNumberSeason();
        this.seasons = showData.getSeasons();
    }

    public int getNumberSeason() {
        return numberOfSeasons;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    public void addRating(double rate, int seasonNum) {
        Season season = this.getSeasons().get(seasonNum - 1);
        season.addRating(rate);
    }


    @Override
    public double getRating() {
        double sum = 0;
        int numRatings = 0;
        for (Season season : this.getSeasons()) {
            List<Double> doubleArrayList = season.getRatings();
            double seasonMean = 0;
            for (Double rating : doubleArrayList) {
                seasonMean += rating;
            }
            if (seasonMean != 0) {
                seasonMean /= doubleArrayList.size();
            }
            sum +=seasonMean;
        }

        if (sum == 0) {
            return 0;
        }
        sum /= this.getSeasons().size();
        return sum;
    }
    @Override
    public int getDuration() {
        int duration = 0;
        for (Season season : this.getSeasons()) {
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
