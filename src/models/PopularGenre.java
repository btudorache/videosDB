package models;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PopularGenre implements Comparable<PopularGenre> {
    private String genre;
    private int numViews;

    public PopularGenre(String genre, int numViews) {
        this.genre = genre;
        this.numViews = numViews;
    }

    public String getGenre() {
        return genre;
    }

    public int getNumViews() {
        return numViews;
    }

    public void addViews(int views) {
        this.numViews += views;
    }

    public static ArrayList<String> orderedMostPopularGeneres(HashMap<String, Video> videoDict) {
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
    public int compareTo(PopularGenre that) {
        return this.numViews - that.numViews;
    }
}
