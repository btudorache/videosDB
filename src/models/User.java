package models;

import common.Constants;
import fileio.ActionInputData;
import fileio.UserInputData;

import java.util.*;

public final class User {
    /**
     * User's username
     */
    private String username;
    /**
     * Subscription Type
     */
    private String subscriptionType;
    /**
     * The history of the movies seen
     */
    private Map<String, Integer> history;
    /**
     * Movies added to favorites
     */
    private ArrayList<String> favoriteMovies;

    private ArrayList<String> ratedMovies;
    private HashMap<String, HashSet<Integer>> ratedShows;
    private int numRatings;

    public User(final UserInputData userData) {
        this.username = userData.getUsername();
        this.subscriptionType = userData.getSubscriptionType();
        this.favoriteMovies = userData.getFavoriteMovies();
        this.history = userData.getHistory();
        this.numRatings = 0;

        this.ratedMovies = new ArrayList<>();
        this.ratedShows = new HashMap<>();
    }

    /**
     *
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
     *
     * @param action
     * @param videoDict
     * @return
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
     *
     * @param action
     * @param videoDict
     * @return
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
     *
     * @param action
     * @param movieDict
     * @param showDict
     * @return
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
     *
     * @param userDict
     * @param action
     * @return
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

        Comparator<User> ratingsComparator = new Comparator<User>() {
            @Override
            public int compare(final User user1,
                               final User user2) {
                if (user1.getNumRatings() - user2.getNumRatings() == 0) {
                    return user1.getUsername().compareTo(user2.getUsername());
                } else {
                    return user1.getNumRatings() - user2.getNumRatings();
                }
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
     *
     * @param videoSet
     * @param videoDict
     * @return
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
     *
     * @param videoSet
     * @param videoDict
     * @return
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
     *
     * @param videoSet
     * @param videoDict
     * @return
     */
    public String recommendPopular(final LinkedHashSet<String> videoSet,
                                   final HashMap<String, Video> videoDict) {
        ArrayList<Video> videoList = getUnseenVideos(videoSet, videoDict);

        if (videoList.isEmpty() || !this.getSubscriptionType().equals(Constants.PREMIUM)) {
            return "PopularRecommendation cannot be applied!";
        }

        ArrayList<String> genresOrdered = PopularGenre.orderedPopGeneres(videoDict);

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
     *
     * @param videoSet
     * @param videoDict
     * @return
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

        Video searchedVideo = null;
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
     *
     * @param videoSet
     * @param videoDict
     * @param genre
     * @return
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
     *
     * @param title
     */
    public void addToRatedMovies(final String title) {
        this.getRatedMovies().add(title);
    }

    /**
     *
     * @param title
     * @param season
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
     *
     * @param title
     * @param season
     * @return
     */
    public boolean hasRatedShow(final String title, final int season) {
        if (this.ratedShows.containsKey(title)) {
            return this.ratedShows.get(title).contains(season);
        } else {
            return false;
        }
    }
}
