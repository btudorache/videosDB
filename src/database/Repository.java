package database;

import common.Constants;
import fileio.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class Repository {
    /**
     * List of actors
     */
    private List<ActorInputData> actorsData;
    /**
     * List of users
     */
    private List<UserInputData> usersData;
    /**
     * List of commands
     */
    private List<ActionInputData> commandsData;
    /**
     * List of movies
     */
    private List<MovieInputData> moviesData;
    /**
     * List of serials aka tv shows
     */
    private List<SerialInputData> serialsData;

    private HashMap<String, UserInputData> userDict;

    private Writer fileWriter;
    private JSONArray arrayResult;

    public Repository(Input input, Writer fileWriter, JSONArray arrayResult) {
        this.actorsData = input.getActors();

        this.usersData = input.getUsers();
        this.userDict = new HashMap<String, UserInputData>();
        for (UserInputData userInfo : this.usersData) {
            this.userDict.put(userInfo.getUsername(), userInfo);
        }

        this.commandsData = input.getCommands();
        this.moviesData = input.getMovies();
        this.serialsData = input.getSerials();

        this.fileWriter = fileWriter;
        this.arrayResult = arrayResult;
    }

    private void runCommands(ActionInputData action) throws IOException {
        if (action.getType().equals(Constants.FAVORITE)) {
            UserInputData user = this.userDict.get(action.getUsername());
            if (user.getHistory().containsKey(action.getTitle())) {
                if (user.getFavoriteMovies().contains(action.getTitle())) {
                    JSONObject data = this.fileWriter.writeFile(action.getActionId(), "", "error -> " + action.getTitle() + " is already in favourite list");
                    this.arrayResult.add((Object) data);
                } else {
                    user.getFavoriteMovies().add(action.getTitle());
                    JSONObject data = this.fileWriter.writeFile(action.getActionId(), "", "success -> " + action.getTitle() + " was added as favourite");
                    this.arrayResult.add(data);
                }
            } else {
                JSONObject data = this.fileWriter.writeFile(action.getActionId(), "", "error -> " + action.getTitle() + " is not seen");
                this.arrayResult.add(data);
            }

        } else if (action.getType().equals(Constants.VIEW)) {

        } else if (action.getType().equals(Constants.RATING)) {

        }
    }

    private void runQueries(ActionInputData action) {
        if (action.getObjectType().equals(Constants.ACTORS)) {

        } else if (action.getObjectType().equals(Constants.MOVIES)) {

        } else if (action.getObjectType().equals(Constants.USERS)) {

        }
    }

    private void runRecommendations(ActionInputData action) {
        if (action.getType().equals(Constants.STANDARD)) {

        } else if (action.getType().equals(Constants.BEST_UNSEEN)) {

        } else if (this.userDict.containsKey(action.getUsername()) &&
                   this.userDict.get(action.getUsername()).getSubscriptionType().equals(Constants.PREMIUM) &&
                   action.getType().equals(Constants.POPULAR)) {

        } else if (this.userDict.containsKey(action.getUsername()) &&
                   this.userDict.get(action.getUsername()).getSubscriptionType().equals(Constants.PREMIUM) &&
                   action.getType().equals(Constants.FAVORITE)) {

        } else if (this.userDict.containsKey(action.getUsername()) &&
                   this.userDict.get(action.getUsername()).getSubscriptionType().equals(Constants.PREMIUM) &&
                   action.getType().equals(Constants.SEARCH)) {

        }
    }

    public void runActions() throws IOException {
        for (ActionInputData action : this.commandsData) {
            if (action.getActionType().equals(Constants.COMMAND)) {
                runCommands(action);
            } else if (action.getActionType().equals(Constants.QUERY)) {
                runQueries(action);
            } else if (action.getActionType().equals(Constants.RECOMMENDATION)) {
                runRecommendations(action);
            }
        }
    }
}
