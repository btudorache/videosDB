package models.video;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Class used to model a Genre with number of views.
 * Used to sort genres.
 * Implements Comparable interface, used to sort by number of views
 */
public final class PopularGenre implements Comparable<PopularGenre> {
    private final String genre;
    private int numViews;

    public PopularGenre(final String genre, final int numViews) {
        this.genre = genre;
        this.numViews = numViews;
    }

    public String getGenre() {
        return genre;
    }

    public int getNumViews() {
        return numViews;
    }

    /**
     * adds views to this genre
     * @param views number of views to be added
     */
    public void addViews(final int views) {
        this.numViews += views;
    }

    /**
     * sorts genres by number of views
     * @param videoDict list of all videos
     * @return list of sorted genres
     */
    public static ArrayList<String> orderedPopularGeneres(final HashMap<String, Video> videoDict) {
        ArrayList<PopularGenre> popularGenreList = new ArrayList<>();
        HashMap<String, Integer> popGenre = new HashMap<>();
        for (Video video : videoDict.values()) {
            for (String genre : video.getGenres()) {
                if (!popGenre.containsKey(genre)) {
                    popGenre.put(genre, video.getNumViews());
                } else {
                    popGenre.put(genre, popGenre.get(genre) + video.getNumViews());
                }
            }
        }
        for (String genre : popGenre.keySet()) {
            popularGenreList.add(new PopularGenre(genre, popGenre.get(genre)));
        }
        popularGenreList.sort(Collections.reverseOrder());

        ArrayList<String> genresOrdered = new ArrayList<>();
        for (PopularGenre genre : popularGenreList) {
            genresOrdered.add(genresOrdered.size(), genre.getGenre());
        }

        return genresOrdered;
    }

    /**
     * Implementation of the comparable interface.
     * The sorting is done by number of views
     * @param that PopularGenre object to be compared to
     * @return result of the comparing
     */
    @Override
    public int compareTo(final PopularGenre that) {
        return this.numViews - that.numViews;
    }
}
