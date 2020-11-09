package models;

import fileio.UserInputData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private int numRatings;
    private ArrayList<String> moviesRated;
    private HashMap<String, ArrayList<Integer>> showsRated;

    public User(UserInputData userData) {
        this.username = userData.getUsername();
        this.subscriptionType = userData.getSubscriptionType();
        this.favoriteMovies = userData.getFavoriteMovies();
        this.history = userData.getHistory();
        this.numRatings = 0;

        this.moviesRated = new ArrayList<String>();
        this.showsRated = new HashMap<String, ArrayList<Integer>>();
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

    public ArrayList<String> getMoviesRated() {
        return moviesRated;
    }

    public HashMap<String, ArrayList<Integer>> getSeriesRated() {
        return showsRated;
    }
}
