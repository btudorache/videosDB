package models;

import common.Constants;
import fileio.ActionInputData;

import java.lang.reflect.Array;
import java.util.*;

public abstract class Video implements Comparable<Video> {
    /**
     * Video's title
     */
    private String title;
    /**
     * The year the video was released
     */
    private int year;
    /**
     * Video casting
     */
    private ArrayList<String> cast;
    /**
     * Video genres
     */
    private ArrayList<String> genres;
    /**
     * Video
     */
    protected double rating;
    protected int numRatings;
    protected int numFavorites;
    protected int numViews;

    public Video(final String title, final int year, final ArrayList<String> cast, final ArrayList<String> genres) {
        this.title = title;
        this.year = year;
        this.cast = cast;
        this.genres = genres;
        this.rating = 0;
        this.numRatings = 0;
        this.numFavorites = 0;
        this.numViews = 0;
    }

    public void incrementNumFavorites() {
        this.numFavorites++;
    }

    public void addNumViews(int views) {
        this.numViews += views;
    }

    public abstract void addRating(double rate, int season);

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public ArrayList<String> getCast() {
        return cast;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public double getRating() {
        return rating;
    }

    public int getNumRatings(){
        return numRatings;
    }

    public int getNumFavorites() {
        return numFavorites;
    }

    public int getNumViews() {
        return numViews;
    }

    public static ArrayList<Video> findShows(HashMap<String, Video> videos, List<List<String>> filters) {
        ArrayList<Video> videoList = new ArrayList<>();
        // if both filter present
        if (filters.get(0) != null && filters.get(1) != null) {
            for (Video video : videos.values()) {
                if (filters.get(0).get(0) != null &&
                        video.getYear() == Integer.parseInt(filters.get(0).get(0)) &&
                        video.getGenres().containsAll(filters.get(1))) {
                    videoList.add(video);
                }
            }
            // if only year filter
        } else if (filters.get(0) != null) {
            for (Video video : videos.values()) {
                if (video.getYear() == Integer.parseInt(filters.get(0).get(0))) {
                    videoList.add(video);
                }
            }
            // if only genre filter
        } else if (filters.get(1) != null) {
            for (Video video : videos.values()) {
                if (video.getGenres().containsAll(filters.get(1))) {
                    videoList.add(video);
                }
            }
            // if no filter
        } else {
            videoList.addAll(videos.values());
        }
        return videoList;
    }

    public static String parseQuery(ArrayList<Video> videoList, int numShows) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append('[');
        int showsNumber = Math.min(numShows, videoList.size());
        for (int i = 0; i < showsNumber - 1; i++) {
            queryBuilder.append(videoList.get(i).getTitle());
            queryBuilder.append(", ");
        }
        queryBuilder.append(videoList.get(showsNumber - 1).getTitle());
        queryBuilder.append(']');
        return queryBuilder.toString();
    }

    public static void sortRating(String order, ArrayList<Video> videoList) {
        if (order.equals(Constants.ASCENDING)) {
            Collections.sort(videoList);
        } else if (order.equals(Constants.DESCENDING)) {
            Collections.sort(videoList, Collections.reverseOrder());
        }
    }

    public static void sortLongest(String order, ArrayList<Video> videoList) {
        if (order.equals(Constants.ASCENDING)) {
            videoList.sort((show1, show2) -> show1.getDuration() - show2.getDuration());
        } else if (order.equals(Constants.DESCENDING)) {
            videoList.sort((show1, show2) -> show2.getDuration() - show1.getDuration());
        }
    }

    public static String queryFavorite(ArrayList<Video> filteredVideos, ActionInputData action) {
        filteredVideos.removeIf(video -> video.getNumFavorites() == 0);

        if (filteredVideos.isEmpty()) {
            return "Query result: []";
        }

        filteredVideos.sort(Collections.reverseOrder(new Comparator<Video>() {
            @Override
            public int compare(Video video1, Video video2) {
                return video1.getNumFavorites() - video2.getNumFavorites();
            }
        }));

        return "Query result: " + parseQuery(filteredVideos, action.getNumber());
    }

    public static String queryMostViewed(ArrayList<Video> filteredVideos, ActionInputData action) {
        filteredVideos.removeIf(video -> video.getNumViews() == 0);

        if (filteredVideos.isEmpty()) {
            return "Query result: []";
        }

        filteredVideos.sort(Collections.reverseOrder(new Comparator<Video>() {
            @Override
            public int compare(Video video1, Video video2) {
                return video1.getNumViews() - video2.getNumViews();
            }
        }));

        return "Query result: " + parseQuery(filteredVideos, action.getNumber());
    }

    abstract int getDuration();

    @Override
    public int compareTo(Video that) {
        if (Double.compare(this.getRating(), that.getRating()) == 0) {
            return this.getTitle().compareTo(that.getTitle());
        } else {
            return Double.compare(this.getRating(), that.getRating());
        }
    }
}
