package models;

import common.Constants;
import fileio.ActionInputData;
import fileio.UserInputData;

import java.util.*;

public class User {
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

    public User(UserInputData userData) {
        this.username = userData.getUsername();
        this.subscriptionType = userData.getSubscriptionType();
        this.favoriteMovies = userData.getFavoriteMovies();
        this.history = userData.getHistory();
        this.numRatings = 0;

        this.ratedMovies = new ArrayList<>();
        this.ratedShows = new HashMap<>();
    }

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

    public String commandFavorite(ActionInputData action, HashMap<String, Video> movieDict, HashMap<String, Video> showDict) {
        if (this.getHistory().containsKey(action.getTitle())) {
            if (this.getFavoriteMovies().contains(action.getTitle())) {
                return "error -> " + action.getTitle() + " is already in favourite list";
            } else {
                this.getFavoriteMovies().add(action.getTitle());
                if (movieDict.containsKey(action.getTitle())) {
                    movieDict.get(action.getTitle()).incrementNumFavorites();
                    if (this.getSubscriptionType().equals(Constants.PREMIUM)) {
                        movieDict.get(action.getTitle()).incrementNumPremiumFavorites();
                    }
                } else if (showDict.containsKey(action.getTitle())) {
                    showDict.get(action.getTitle()).incrementNumFavorites();
                    if (this.getSubscriptionType().equals(Constants.PREMIUM)) {
                        showDict.get(action.getTitle()).incrementNumPremiumFavorites();
                    }
                }
                return "success -> " + action.getTitle() + " was added as favourite";
            }
        } else {
            return "error -> " + action.getTitle() + " is not seen";
        }
    }

    public static String getUsersQuery(HashMap<String, User> userDict, String order, int numUsers) {
        ArrayList<User> userList = new ArrayList<User>();
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
            public int compare(User user1, User user2) {
                if (user1.getNumRatings() - user2.getNumRatings() == 0) {
                    return user1.getUsername().compareTo(user2.getUsername());
                } else {
                    return user1.getNumRatings() - user2.getNumRatings();
                }
            }
        };
        if (order.equals(Constants.ASCENDING)) {
            userList.sort(ratingsComparator);
        } else if (order.equals(Constants.DESCENDING)) {
            userList.sort(Collections.reverseOrder(ratingsComparator));
        }

        StringBuilder builder = new StringBuilder();
        builder.append('[');
        int usersNumber = Math.min(numUsers, userList.size());
        for (int i = 0; i < usersNumber - 1; i++) {
            builder.append(userList.get(i).getUsername());
            builder.append(", ");
        }
        builder.append(userList.get(usersNumber - 1).getUsername());
        builder.append(']');
        return builder.toString();
    }

    public ArrayList<Video> getUnseenVideos(LinkedHashSet<String> videoSet, HashMap<String, Video> videoDict) {
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

    public ArrayList<Video> getUnseenVideosByGenre(LinkedHashSet<String> videoSet,
                                                   HashMap<String, Video> videoDict,
                                                   String genre) {
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

    public String recommendStandard(LinkedHashSet<String> videoSet, HashMap<String, Video> videoDict) {
        ArrayList<Video> videoList = getUnseenVideos(videoSet, videoDict);
        if (videoList.isEmpty()) {
            return "StandardRecommendation cannot be applied!";
        } else {
            return "StandardRecommendation result: " + videoList.get(0).getTitle();
        }
    }

    public String recommendBestUnseen(LinkedHashSet<String> videoSet, HashMap<String, Video> videoDict) {
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

            return "BestRatedUnseenRecommendation result: "+ searchedVideo.getTitle();
        }
    }

    public String recommendPopular(LinkedHashSet<String> videoSet,
                                   HashMap<String, Video> videoDict) {
        ArrayList<Video> videoList = getUnseenVideos(videoSet, videoDict);

        if (videoList.isEmpty() || !this.getSubscriptionType().equals(Constants.PREMIUM)) {
            return "PopularRecommendation cannot be applied!";
        }

        ArrayList<String> genresOrdered = PopularGenre.orderedMostPopularGeneres(videoDict);

        for (String genre : genresOrdered) {
            for (Video video : videoList) {
                if (video.getGenres().contains(genre)) {
                    return "PopularRecommendation result: " + video.getTitle();
                }
            }
        }
        return "PopularRecommendation cannot be applied!";
    }

    public String recommendFavorite(LinkedHashSet<String> videoSet,
                                    HashMap<String, Video> videoDict) {
        ArrayList<Video> videoList = new ArrayList<>();
        for (String video : videoSet) {
            if (videoDict.get(video).getNumFavorites() != 0 &&
                !this.getHistory().containsKey(videoDict.get(video).getTitle())) {
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

    public String recommendSearch(LinkedHashSet<String> videoSet,
                                  HashMap<String, Video> videoDict,
                                  String genre) {
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

    public void addToRatedMovies(String title) {
        this.getRatedMovies().add(title);
    }

    public void addToRatedShows(String title, int season) {
        if (!this.ratedShows.containsKey(title)) {
            HashSet<Integer> set = new HashSet<>();
            set.add(season);
            this.ratedShows.put(title, set);
        } else {
            this.ratedShows.get(title).add(season);
        }
    }

    public boolean hasRatedShow(String title, int season) {
        if (this.ratedShows.containsKey(title)) {
            if (this.ratedShows.get(title).contains(season)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
