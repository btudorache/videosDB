package models.video;

import common.Constants;
import fileio.ActionInputData;

import java.util.*;

/**
 * Abstract class that is used as a base for the Show and Movie classes
 * Implements the comparable interface, used to sort by rating
 */
public abstract class Video implements Comparable<Video> {
    private final String title;
    private final int year;
    private final ArrayList<String> cast;
    private final ArrayList<String> genres;

    protected double rating;
    /**
     *  number of ratings
     */
    protected int numRatings;
    /**
     *  number of times added to favorite by an user
     */
    protected int numFavorites;
    /**
     *  number of times viewed by users
     */
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
     * increments the number of times this video was added to favorites
     */
    public void incrementNumFavorites() {
        this.numFavorites++;
    }

    /**
     * updates the number of views of this video
     * @param views number of times this video was viewed
     */
    public void addNumViews(final int views) {
        this.numViews += views;
    }

    /**
     * adds rating to the video. for movies, the season param will always be 0
     * @param rate rate given to this video
     * @param season season rated
     */
    public abstract void addRating(double rate, int season);

    abstract int getDuration();

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

    public final int getNumFavorites() {
        return numFavorites;
    }

    public final int getNumViews() {
        return numViews;
    }

    /**
     * function that filters given video list by the given filters
     * @param videos map of videos to be filtered
     * @param filters list of filters
     * @return list of filters shows
     */
    public static ArrayList<Video> findShows(final HashMap<String, Video> videos,
                                             final List<List<String>> filters) {
        ArrayList<Video> videoList = new ArrayList<>();
        // if both filters present
        if (filters.get(Constants.FILTER_YEAR).get(0) != null
            && filters.get(Constants.FILTER_GENRE).get(0) != null) {
            for (Video video : videos.values()) {
                if (video.getYear() == Integer.parseInt(filters.get(Constants.FILTER_YEAR).get(0))
                    && video.getGenres().containsAll(filters.get(Constants.FILTER_GENRE))) {
                    videoList.add(video);
                }
            }
        // if only year filter
        } else if (filters.get(Constants.FILTER_YEAR).get(0) != null) {
            for (Video video : videos.values()) {
                if (video.getYear()
                        == Integer.parseInt(filters.get(Constants.FILTER_YEAR).get(0))) {
                    videoList.add(video);
                }
            }
        // if only genre filter
        } else if (filters.get(Constants.FILTER_GENRE).get(0) != null) {
            for (Video video : videos.values()) {
                if (video.getGenres().containsAll(filters.get(Constants.FILTER_GENRE))) {
                    videoList.add(video);
                }
            }
            // if no filter
        } else {
            videoList.addAll(videos.values());
        }
        return videoList;
    }

    /**
     * parses the video list to string
     * @param videoList video list given
     * @param numShows number of shows to be parsed
     * @return string of video list
     */
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
     * function that sorts a given list using a specific comparator and specific order
     * @param order order in which the list to be sorted (ascending or descending)
     * @param videoList video list to be sorted
     * @param comparator comparator using for sorting
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
     * query videos by longest view time
     * @param filteredVideos video list to be sorted
     * @param action query data
     * @return string of ordered query result
     */
    public static String queryLongest(final ArrayList<Video> filteredVideos,
                                      final ActionInputData action) {
        filteredVideos.removeIf(video -> video.getDuration() == 0);

        if (filteredVideos.isEmpty()) {
            return "Query result: []";
        }

        // comparator used to filter list
        Comparator<Video> lengthComparator = (video1, video2) -> {
            if (video1.getDuration() - video2.getDuration() == 0) {
                return video1.getTitle().compareTo(video2.getTitle());
            } else {
                return video1.getDuration() - video2.getDuration();
            }
        };
        sortByOrder(action.getSortType(), filteredVideos, lengthComparator);

        return "Query result: " + parseQuery(filteredVideos, action.getNumber());
    }

    /**
     * query videos by rating
     * @param filteredVideos videos to be sorted
     * @param action query data
     * @return string of ordered query result
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
     * query videos by number of times added to favorite
     * @param filteredVideos videos to be sorted
     * @param action query data
     * @return string of ordered query result
     */
    public static String queryFavorite(final ArrayList<Video> filteredVideos,
                                       final ActionInputData action) {
        filteredVideos.removeIf(video -> video.getNumFavorites() == 0);

        if (filteredVideos.isEmpty()) {
            return "Query result: []";
        }

        // comparator used to filter list
        Comparator<Video> favoriteComparator = (video1, video2) -> {
            if (video1.getNumFavorites() - video2.getNumFavorites() == 0) {
                return video1.getTitle().compareTo(video2.getTitle());
            } else {
                return video1.getNumFavorites() - video2.getNumFavorites();
            }
        };
        sortByOrder(action.getSortType(), filteredVideos, favoriteComparator);

        return "Query result: " + parseQuery(filteredVideos, action.getNumber());
    }

    /**
     * query videos by number of times viewed
     * @param filteredVideos videos to be sorted
     * @param action query data
     * @return string of ordered query result
     */
    public static String queryMostViewed(final ArrayList<Video> filteredVideos,
                                         final ActionInputData action) {
        filteredVideos.removeIf(video -> video.getNumViews() == 0);

        if (filteredVideos.isEmpty()) {
            return "Query result: []";
        }

        // comparator used to filter list
        Comparator<Video> viewsComparator = (video1, video2) -> {
            if (video1.getNumViews() - video2.getNumViews() == 0) {
                return video1.getTitle().compareTo(video2.getTitle());
            } else {
                return video1.getNumViews() - video2.getNumViews();
            }
        };
        sortByOrder(action.getSortType(), filteredVideos, viewsComparator);

        return "Query result: " + parseQuery(filteredVideos, action.getNumber());
    }

    /**
     * Implementation of the comparable interface.
     * The sorting is done by rating
     * @param that video to be compared to
     * @return result of the comparing
     */
    @Override
    public int compareTo(final Video that) {
        if (Double.compare(this.getRating(), that.getRating()) == 0) {
            return this.getTitle().compareTo(that.getTitle());
        } else {
            return Double.compare(this.getRating(), that.getRating());
        }
    }
}
