package models;

import common.Constants;
import entertainment.Season;
import fileio.SerialInputData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Show extends Video implements Comparable<Show> {
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

    public static ArrayList<Show> findShows(HashMap<String, Show> shows, List<List<String>> filters) {
        ArrayList<Show> showList = new ArrayList<Show>();
        // if both filter present
        if (filters.get(0) != null && filters.get(1) != null) {
            for (Show show : shows.values()) {
                if (filters.get(0).get(0) != null &&
                    show.getYear() == Integer.parseInt(filters.get(0).get(0)) &&
                    show.getGenres().containsAll(filters.get(1))) {
                    showList.add(show);
                }
            }
            // if only year filter
        } else if (filters.get(0) != null) {
            for (Show show : shows.values()) {
                if (show.getYear() == Integer.parseInt(filters.get(0).get(0))) {
                    showList.add(show);
                }
            }
            // if only genre filter
        } else if (filters.get(1) != null) {
            for (Show show : shows.values()) {
                if (show.getGenres().containsAll(filters.get(1))) {
                    showList.add(show);
                }
            }
            // if no filter
        } else {
            showList.addAll(shows.values());
        }
        return showList;
    }

    public static String parseQuery(ArrayList<Show> showList, int numShows) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append('[');
        int showsNumber = Math.min(numShows, showList.size());
        for (int i = 0; i < showsNumber - 1; i++) {
            queryBuilder.append(showList.get(i).getTitle());
            queryBuilder.append(", ");
        }
        queryBuilder.append(showList.get(showsNumber - 1).getTitle());
        queryBuilder.append(']');
        return queryBuilder.toString();
    }

    public static void sortRating(String order, ArrayList<Show> showList) {
        if (order.equals(Constants.ASCENDING)) {
            Collections.sort(showList);
        } else if (order.equals(Constants.DESCENDING)) {
            Collections.sort(showList, Collections.reverseOrder());
        }
    }

    public static void sortLongest(String order, ArrayList<Show> showList) {
        if (order.equals(Constants.ASCENDING)) {
            showList.sort((show1, show2) -> show1.getDuration() - show2.getDuration());
        } else if (order.equals(Constants.DESCENDING)) {
            showList.sort((show1, show2) -> show2.getDuration() - show1.getDuration());
        }
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

    public int getDuration() {
        int duration = 0;
        for (Season season : this.getSeasons()) {
            duration += season.getDuration();
        }
        return duration;
    }

    @Override
    public int compareTo(Show that) {
        if (Double.compare(this.getRating(), that.getRating()) == 0) {
            return this.getTitle().compareTo(that.getTitle());
        } else {
            return Double.compare(this.getRating(), that.getRating());
        }
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
