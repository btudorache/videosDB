package models;

import common.Constants;
import fileio.UserInputData;

import java.util.ArrayList;
import java.util.Comparator;
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

    public User(UserInputData userData) {
        this.username = userData.getUsername();
        this.subscriptionType = userData.getSubscriptionType();
        this.favoriteMovies = userData.getFavoriteMovies();
        this.history = userData.getHistory();
        this.numRatings = 0;
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
}
