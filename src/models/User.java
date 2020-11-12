package models;

import common.Constants;
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
    private HashMap<String, HashMap<Integer, Boolean>> ratedShows;
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

        if (order.equals(Constants.ASCENDING)) {
            userList.sort((user1, user2) -> user1.getNumRatings() - user2.getNumRatings());
        } else if (order.equals(Constants.DESCENDING)) {
            userList.sort((user1, user2) -> user2.getNumRatings() - user1.getNumRatings());
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

    public ArrayList<Video> getUnseenVideos(LinkedHashSet<String> videoSet, HashMap<String, Video> movieDict, HashMap<String, Video> showDict) {
        videoSet.removeAll(getHistory().keySet());
        ArrayList<Video> videoList = new ArrayList<>();
        for (String title : videoSet) {
            if (movieDict.containsKey(title)) {
                videoList.add(movieDict.get(title));
            } else if (showDict.containsKey(title)) {
                videoList.add(showDict.get(title));
            }
        }

        return videoList;
    }

    public ArrayList<Video> getUnseenVideosByGenre(LinkedHashSet<String> videoSet,
                                            HashMap<String, Video> movieDict,
                                            HashMap<String, Video> showDict,
                                            String genre) {
        videoSet.removeAll(getHistory().keySet());
        ArrayList<Video> videoList = new ArrayList<>();
        for (String title : videoSet) {
            if (movieDict.containsKey(title) && movieDict.get(title).getGenres().contains(genre)) {
                videoList.add(movieDict.get(title));
            } else if (showDict.containsKey(title) && showDict.get(title).getGenres().contains(genre)) {
                videoList.add(showDict.get(title));
            }
        }

        return videoList;
    }

    public String recommendStandard(LinkedHashSet<String> videoSet, HashMap<String, Video> movieDict, HashMap<String, Video> showDict) {
        ArrayList<Video> videoList = getUnseenVideos(videoSet, movieDict, showDict);
        if (videoList.isEmpty()) {
            return "StandardRecommendation cannot be applied!";
        } else {
            return "StandardRecommendation result: " + videoList.get(0).getTitle();
        }
    }

    public String recommendBestUnseen(LinkedHashSet<String> videoSet, HashMap<String, Video> movieDict, HashMap<String, Video> showDict) {
        ArrayList<Video> videoList = getUnseenVideos(videoSet, movieDict, showDict);
        if (videoList.isEmpty()) {
            return "BestRatedUnseenRecommendation cannot be applied!";
        } else {
            Collections.sort(videoList, Collections.reverseOrder(new Comparator<Video>() {
                @Override
                public int compare(Video video1, Video video2) {
                    if (Double.compare(video1.getRating(), video2.getRating()) == 0) {
                        return video2.getTitle().compareTo(video1.getTitle());
                    } else {
                        return Double.compare(video1.getRating(), video2.getRating());
                    }
                }
            }));
            return "BestRatedUnseenRecommendation result: "+ videoList.get(0).getTitle();
        }
    }

    public String recommendFavorite(HashMap<String, Video> movieDict, HashMap<String, Video> showDict) {
        ArrayList<Video> videoList = new ArrayList<>();
        for (Video video : movieDict.values()) {
            if (video.getNumFavorites() != 0 && !this.getFavoriteMovies().contains(video.getTitle())) {
                videoList.add(video);
            }
        }
        for (Video video : showDict.values()) {
            if (video.getNumFavorites() != 0 && !this.getFavoriteMovies().contains(video.getTitle())) {
                videoList.add(video);
            }
        }

        if (videoList.isEmpty()) {
            return "FavoriteRecommendation cannot be applied!";
        } else {
            videoList.sort(Collections.reverseOrder(new Comparator<Video>() {
                @Override
                public int compare(Video video1, Video video2) {
                    if (video1.getNumFavorites() - video2.getNumFavorites() == 0) {
                        return video2.getTitle().compareTo(video1.getTitle());
                    } else {
                        return video1.getNumFavorites() - video2.getNumFavorites();
                    }
                }
            }));
        }
        return "FavoriteRecommendation result: " + videoList.get(0).getTitle();
    }

    public String recommendSearch(LinkedHashSet<String> videoSet,
                                  HashMap<String, Video> movieDict,
                                  HashMap<String, Video> showDict,
                                  String genre) {
        ArrayList<Video> videoList = getUnseenVideosByGenre(videoSet, movieDict, showDict, genre);
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

    public HashMap<String, HashMap<Integer, Boolean>> getRatedShows() {
        return ratedShows;
    }

    public void addToRatedMovies(String title) {
        this.getRatedMovies().add(title);
    }

    public void addToRatedShows(String title, int season) {

    }
}
