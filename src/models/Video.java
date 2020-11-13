package models;

import common.Constants;
import fileio.ActionInputData;

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

    public Video(final String title,
                 final int year,
                 final ArrayList<String> cast,
                 final ArrayList<String> genres) {
        this.title = title;
        this.year = year;
        this.cast = cast;
        this.genres = genres;
        this.rating = 0;
        this.numRatings = 0;
        this.numFavorites = 0;
        this.numViews = 0;
    }

    /**
     *
     */
    public void incrementNumFavorites() {
        this.numFavorites++;
    }

    /**
     *
     * @param views
     */
    public void addNumViews(final int views) {
        this.numViews += views;
    }

    /**
     *
     * @param rate
     * @param season
     */
    public abstract void addRating(double rate, int season);

    public final String getTitle() {
        return title;
    }

    public final int getYear() {
        return year;
    }

    public final ArrayList<String> getCast() {
        return cast;
    }

    public final ArrayList<String> getGenres() {
        return genres;
    }

    public double getRating() {
        return rating;
    }

    public final int getNumRatings() {
        return numRatings;
    }

    public final int getNumFavorites() {
        return numFavorites;
    }

    public final int getNumViews() {
        return numViews;
    }

    /**
     *
     * @param videos
     * @param filters
     * @return
     */
    public static ArrayList<Video> findShows(final HashMap<String, Video> videos,
                                             final List<List<String>> filters) {
        ArrayList<Video> videoList = new ArrayList<>();
        // if both filter present
        if (filters.get(0) != null && filters.get(0).get(0) != null
            && filters.get(1) != null && filters.get(1).get(0) != null) {
            for (Video video : videos.values()) {
                if (filters.get(0).get(0) != null
                    && video.getYear() == Integer.parseInt(filters.get(0).get(0))
                    && video.getGenres().containsAll(filters.get(1))) {
                    videoList.add(video);
                }
            }
            // if only year filter
        } else if (filters.get(0) != null && filters.get(0).get(0) != null) {
            for (Video video : videos.values()) {
                if (video.getYear() == Integer.parseInt(filters.get(0).get(0))) {
                    videoList.add(video);
                }
            }
            // if only genre filter
        } else if (filters.get(1) != null && filters.get(1).get(0) != null) {
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

    private static String parseQuery(final ArrayList<Video> videoList,
                                     final int numShows) {
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

    /**
     *
     * @param order
     * @param videoList
     * @param comparator
     */
    public static void sortByOrder(final String order,
                                   final ArrayList<Video> videoList,
                                   final Comparator<Video> comparator) {
        if (order.equals(Constants.ASCENDING)) {
            videoList.sort(comparator);
        } else if (order.equals(Constants.DESCENDING)) {
            videoList.sort(Collections.reverseOrder(comparator));
        }
    }

    /**
     *
     * @param filteredVideos
     * @param action
     * @return
     */
    public static String queryLongest(final ArrayList<Video> filteredVideos,
                                      final ActionInputData action) {
        filteredVideos.removeIf(video -> video.getDuration() == 0);

        if (filteredVideos.isEmpty()) {
            return "Query result: []";
        }

        Comparator<Video> lengthComparator = new Comparator<Video>() {
            @Override
            public int compare(final Video video1, final Video video2) {
                if (video1.getDuration() - video2.getDuration() == 0) {
                    return video1.getTitle().compareTo(video2.getTitle());
                } else {
                    return video1.getDuration() - video2.getDuration();
                }
            }
        };
        sortByOrder(action.getSortType(), filteredVideos, lengthComparator);

        return "Query result: " + parseQuery(filteredVideos, action.getNumber());
    }

    /**
     *
     * @param filteredVideos
     * @param action
     * @return
     */
    public static String queryRating(final ArrayList<Video> filteredVideos,
                                     final ActionInputData action) {
        filteredVideos.removeIf(video -> video.getRating() == 0);

        if (filteredVideos.isEmpty()) {
            return "Query result: []";
        }

        if (action.getSortType().equals(Constants.ASCENDING)) {
            Collections.sort(filteredVideos);
        } else if (action.getSortType().equals(Constants.DESCENDING)) {
            Collections.sort(filteredVideos, Collections.reverseOrder());
        }

        return "Query result: " + parseQuery(filteredVideos, action.getNumber());
    }

    /**
     *
     * @param filteredVideos
     * @param action
     * @return
     */
    public static String queryFavorite(final ArrayList<Video> filteredVideos,
                                       final ActionInputData action) {
        filteredVideos.removeIf(video -> video.getNumFavorites() == 0);

        if (filteredVideos.isEmpty()) {
            return "Query result: []";
        }

        Comparator<Video> favoriteComparator = new Comparator<Video>() {
            @Override
            public int compare(final Video video1, final Video video2) {
                if (video1.getNumFavorites() - video2.getNumFavorites() == 0) {
                    return video1.getTitle().compareTo(video2.getTitle());
                } else {
                    return video1.getNumFavorites() - video2.getNumFavorites();
                }
            }
        };
        sortByOrder(action.getSortType(), filteredVideos, favoriteComparator);

        return "Query result: " + parseQuery(filteredVideos, action.getNumber());
    }

    /**
     *
     * @param filteredVideos
     * @param action
     * @return
     */
    public static String queryMostViewed(final ArrayList<Video> filteredVideos,
                                         final ActionInputData action) {
        filteredVideos.removeIf(video -> video.getNumViews() == 0);

        if (filteredVideos.isEmpty()) {
            return "Query result: []";
        }

        Comparator<Video> viewsComparator = new Comparator<Video>() {
            @Override
            public int compare(final Video video1, final Video video2) {
                if (video1.getNumViews() - video2.getNumViews() == 0) {
                    return video1.getTitle().compareTo(video2.getTitle());
                } else {
                    return video1.getNumViews() - video2.getNumViews();
                }
            }
        };
        sortByOrder(action.getSortType(), filteredVideos, viewsComparator);

        return "Query result: " + parseQuery(filteredVideos, action.getNumber());
    }

    abstract int getDuration();

    @Override
    public int compareTo(final Video that) {
        if (Double.compare(this.getRating(), that.getRating()) == 0) {
            return this.getTitle().compareTo(that.getTitle());
        } else {
            return Double.compare(this.getRating(), that.getRating());
        }
    }
}
