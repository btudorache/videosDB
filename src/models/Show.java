package models;

import entertainment.Season;
import fileio.SerialInputData;

import java.util.ArrayList;

public class Show extends Video {
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
        for (Season season : this.getSeasons()) {
            sum += season.getRatingMean();
        }
        sum /= this.numberOfSeasons;
        return sum;
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
