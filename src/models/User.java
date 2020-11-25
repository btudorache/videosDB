package models;

import common.Constants;
import models.video.PopularGenre;
import fileio.ActionInputData;
import fileio.UserInputData;
import models.video.Video;

import java.util.*;

/**
 * Class used to model users
 */
public final class User {
    private final String username;
    private final String subscriptionType;
    private final Map<String, Integer> history;
    private final ArrayList<String> favoriteMovies;

    /**
     * list of rated movies
     */
    private final ArrayList<String> ratedMovies;
    /**
     * map of rated shows
     */
    private final HashMap<String, HashSet<Integer>> ratedShows;
    private int numRatings;

    public User(final UserInputData userData) {
        this.username = userData.getUsername();
        this.subscriptionType = userData.getSubscriptionType();
        this.favoriteMovies = userData.getFavoriteMovies();
        this.history = userData.getHistory();

        this.ratedMovies = new ArrayList<>();
        this.ratedShows = new HashMap<>();
        this.numRatings = 0;
    }

    /**
     * increments the number of ratings given
     */
    public void incrementNumRatings() {
        this.numRatings++;
    }

    public String getUsername() {
        return username;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    public int getNumRatings() {
        return numRatings;
    }

    /**
     * adds video to favorite
     * @param action action data
     * @param videoDict dictionary of all videos
     * @return string of command result to be put in output file
     */
    public String commandFavorite(final ActionInputData action,
                                  final HashMap<String, Video> videoDict) {
        if (this.getHistory().containsKey(action.getTitle())) {
            if (this.getFavoriteMovies().contains(action.getTitle())) {
                return "error -> " + action.getTitle() + " is already in favourite list";
            } else {
                this.getFavoriteMovies().add(action.getTitle());
                if (videoDict.containsKey(action.getTitle())) {
                    videoDict.get(action.getTitle()).incrementNumFavorites();
                }
                return "success -> " + action.getTitle() + " was added as favourite";
            }
        }
        return "error -> " + action.getTitle() + " is not seen";
    }

    /**
     * views video
     * @param action action data
     * @param videoDict dictionary of all videos
     * @return string of command result to be put in output file
     */
    public String commandView(final ActionInputData action,
                              final HashMap<String, Video> videoDict) {
        if (videoDict.containsKey(action.getTitle())) {
            videoDict.get(action.getTitle()).addNumViews(1);
        }
        if (getHistory().containsKey(action.getTitle())) {
            int numViews = this.getHistory().get(action.getTitle());
            this.getHistory().put(action.getTitle(), numViews + 1);
        } else {
            this.getHistory().put(action.getTitle(), 1);
        }
        return "success -> " + action.getTitle()
                + " was viewed with total views of " + this.getHistory().get(action.getTitle());
    }

    /**
     * rates video
     * @param action action data
     * @param movieDict dictionary of all movies
     * @param showDict dictionaty of all shows
     * @return string of command result to be put in output file
     */
    public String commandRating(final ActionInputData action,
                                final HashMap<String, Video> movieDict,
                                final HashMap<String, Video> showDict) {
        if (this.getHistory().containsKey(action.getTitle())) {
            this.incrementNumRatings();
            if (movieDict.containsKey(action.getTitle())) {
                if (getRatedMovies().contains(action.getTitle())) {
                    return "error -> " + action.getTitle() + " has been already rated";
                } else {
                    movieDict.get(action.getTitle()).addRating(action.getGrade(), 0);
                    this.addToRatedMovies(action.getTitle());
                    return "success -> " + action.getTitle()
                            + " was rated with " + action.getGrade()
                            + " by " + action.getUsername();
                }
            } else if (showDict.containsKey(action.getTitle())) {
                if (this.hasRatedShow(action.getTitle(), action.getSeasonNumber())) {
                    return "error -> " + action.getTitle() + " has been already rated";
                } else {
                    showDict.get(action.getTitle()).addRating(action.getGrade(),
                                                              action.getSeasonNumber());
                    this.addToRatedShows(action.getTitle(), action.getSeasonNumber());
                    return "success -> " + action.getTitle()
                            + " was rated with " + action.getGrade()
                            + " by " + action.getUsername();
                }
            }
        }
        return "error -> " + action.getTitle() + " is not seen";
    }

    /**
     * query users by number of videos seen
     * @param userDict dictionary of all users
     * @param action action data
     * @return string of command result to be put in output file
     */
    public static String getUsersQuery(final HashMap<String, User> userDict,
                                       final ActionInputData action) {
        ArrayList<User> userList = new ArrayList<>();
        for (User user : userDict.values()) {
            if (user.getNumRatings() > 0) {
                userList.add(user);
            }
        }
        if (userList.isEmpty()) {
            return "[]";
        }

        // comparator used to sort users
        Comparator<User> ratingsComparator = (user1, user2) -> {
            if (user1.getNumRatings() - user2.getNumRatings() == 0) {
                return user1.getUsername().compareTo(user2.getUsername());
            } else {
                return user1.getNumRatings() - user2.getNumRatings();
            }
        };
        if (action.getSortType().equals(Constants.ASCENDING)) {
            userList.sort(ratingsComparator);
        } else if (action.getSortType().equals(Constants.DESCENDING)) {
            userList.sort(Collections.reverseOrder(ratingsComparator));
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Query result: [");
        int usersNumber = Math.min(action.getNumber(), userList.size());
        for (int i = 0; i < usersNumber - 1; i++) {
            builder.append(userList.get(i).getUsername());
            builder.append(", ");
        }
        builder.append(userList.get(usersNumber - 1).getUsername());
        builder.append(']');
        return builder.toString();
    }

    /**
     * gets list of all videos unseen by this user
     * @param videoSet set of all video names
     * @param videoDict dictionary of all videos
     * @return list of unseen videos
     */
    private ArrayList<Video> getUnseenVideos(final LinkedHashSet<String> videoSet,
                                             final HashMap<String, Video> videoDict) {
        LinkedHashSet<String> newSet = new LinkedHashSet<>(videoSet);
        newSet.removeAll(getHistory().keySet());
        ArrayList<Video> videoList = new ArrayList<>();
        for (String title : newSet) {
            if (videoDict.containsKey(title)) {
                videoList.add(videoList.size(), videoDict.get(title));
            }
        }

        return videoList;
    }

    /**
     * gets list of all videos unseen by this user from a specific genre
     * @param videoSet set of all video names
     * @param videoDict dictionary of all videos
     * @param genre genre to be filtered
     * @return list of unseen videos from a genre
     */
    private ArrayList<Video> getUnseenVideosByGenre(final LinkedHashSet<String> videoSet,
                                                    final HashMap<String, Video> videoDict,
                                                    final String genre) {
        LinkedHashSet<String> newSet = new LinkedHashSet<>(videoSet);
        newSet.removeAll(getHistory().keySet());
        ArrayList<Video> videoList = new ArrayList<>();
        for (String title : newSet) {
            if (videoDict.containsKey(title) && videoDict.get(title).getGenres().contains(genre)) {
                videoList.add(videoList.size(), videoDict.get(title));
            }
        }

        return videoList;
    }

    /**
     * recommend first unseen video
     * @param videoSet set of all video names
     * @param videoDict dictionary of all videos
     * @return string result of recommendation
     */
    public String recommendStandard(final LinkedHashSet<String> videoSet,
                                    final HashMap<String, Video> videoDict) {
        ArrayList<Video> videoList = getUnseenVideos(videoSet, videoDict);
        if (videoList.isEmpty()) {
            return "StandardRecommendation cannot be applied!";
        } else {
            return "StandardRecommendation result: " + videoList.get(0).getTitle();
        }
    }

    /**
     * recommend best unseen video
     * @param videoSet set of all video names
     * @param videoDict dictionary of all videos
     * @return string result of recommendation
     */
    public String recommendBestUnseen(final LinkedHashSet<String> videoSet,
                                      final HashMap<String, Video> videoDict) {
        ArrayList<Video> videoList = getUnseenVideos(videoSet, videoDict);

        if (videoList.isEmpty()) {
            return "BestRatedUnseenRecommendation cannot be applied!";
        } else {
            Video searchedVideo = videoList.get(0);
            for (Video video : videoList) {
                if (video.getRating() > searchedVideo.getRating()) {
                    searchedVideo = video;
                }
            }

            return "BestRatedUnseenRecommendation result: " + searchedVideo.getTitle();
        }
    }

    /**
     * recommend first unseen video in the most popular genre
     * @param videoSet set of all video names
     * @param videoDict dictionary of all videos
     * @return string result of recommendation
     */
    public String recommendPopular(final LinkedHashSet<String> videoSet,
                                   final HashMap<String, Video> videoDict) {
        ArrayList<Video> videoList = getUnseenVideos(videoSet, videoDict);

        if (videoList.isEmpty() || !this.getSubscriptionType().equals(Constants.PREMIUM)) {
            return "PopularRecommendation cannot be applied!";
        }

        ArrayList<String> genresOrdered = PopularGenre.orderedPopularGeneres(videoDict);

        for (String genre : genresOrdered) {
            for (Video video : videoList) {
                if (video.getGenres().contains(genre)) {
                    return "PopularRecommendation result: " + video.getTitle();
                }
            }
        }
        return "PopularRecommendation cannot be applied!";
    }

    /**
     * gets first unseen video that is is other user's favorite list
     * @param videoSet set of all video names
     * @param videoDict dictionary of all videos
     * @return string result of recommendation
     */
    public String recommendFavorite(final LinkedHashSet<String> videoSet,
                                    final HashMap<String, Video> videoDict) {
        ArrayList<Video> videoList = new ArrayList<>();
        for (String video : videoSet) {
            if (videoDict.get(video).getNumFavorites() != 0
                && !this.getHistory().containsKey(videoDict.get(video).getTitle())) {
                videoList.add(videoList.size(), videoDict.get(video));
            }
        }

        Video searchedVideo;
        if (videoList.isEmpty() || !this.getSubscriptionType().equals(Constants.PREMIUM)) {
            return "FavoriteRecommendation cannot be applied!";
        } else {
            searchedVideo = videoList.get(0);
            for (Video video : videoList) {
                if (video.getNumFavorites() > searchedVideo.getNumFavorites()) {
                    searchedVideo = video;
                }
            }

        }
        return "FavoriteRecommendation result: " + searchedVideo.getTitle();
    }

    /**
     * recommend all unseen videos in a specific genre
     * @param videoSet set of all video names
     * @param videoDict dictionary of all videos
     * @param genre genre to be filtered
     * @return string result of recommendation
     */
    public String recommendSearch(final LinkedHashSet<String> videoSet,
                                  final HashMap<String, Video> videoDict,
                                  final String genre) {
        ArrayList<Video> videoList = getUnseenVideosByGenre(videoSet, videoDict, genre);
        if (videoList.isEmpty() || !this.getSubscriptionType().equals(Constants.PREMIUM)) {
            return "SearchRecommendation cannot be applied!";
        } else {
            Collections.sort(videoList);

            StringBuilder builder = new StringBuilder();
            builder.append("SearchRecommendation result: [");
            for (int i = 0; i < videoList.size() - 1; i++) {
                builder.append(videoList.get(i).getTitle());
                builder.append(", ");
            }
            builder.append(videoList.get(videoList.size() - 1).getTitle());
            builder.append(']');
            return builder.toString();
        }
    }

    public ArrayList<String> getRatedMovies() {
        return ratedMovies;
    }

    /**
     * add movie to rated movie list
     * @param title title of rated movie
     */
    public void addToRatedMovies(final String title) {
        this.getRatedMovies().add(title);
    }

    /**
     * add show to rated show dictionary
     * @param title title of rated show
     * @param season season rated
     */
    public void addToRatedShows(final String title, final int season) {
        if (!this.ratedShows.containsKey(title)) {
            HashSet<Integer> set = new HashSet<>();
            set.add(season);
            this.ratedShows.put(title, set);
        } else {
            this.ratedShows.get(title).add(season);
        }
    }

    /**
     * check if user has rated the show already
     * @param title title of show
     * @param season number of season rated
     * @return truth value
     */
    public boolean hasRatedShow(final String title, final int season) {
        if (this.ratedShows.containsKey(title)) {
            return this.ratedShows.get(title).contains(season);
        } else {
            return false;
        }
    }
}
