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
