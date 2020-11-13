package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public final class PopularGenre implements Comparable<PopularGenre> {
    private String genre;
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
     *
     * @param views
     */
    public void addViews(final int views) {
        this.numViews += views;
    }

    /**
     *
     * @param videoDict
     * @return
     */
    public static ArrayList<String> orderedPopGeneres(final HashMap<String, Video> videoDict) {
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

    @Override
    public int compareTo(final PopularGenre that) {
        return this.numViews - that.numViews;
    }
}
