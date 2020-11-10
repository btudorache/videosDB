package database;

import common.Constants;
import fileio.*;
import models.Movie;
import models.Show;
import models.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.*;


public class Repository {
    /**
     * List of actors
     */
    private List<ActorInputData> actorsData;
    /**
     * List of commands
     */
    private List<ActionInputData> commandsData;

    private HashMap<String, User> userDict;

    private HashMap<String, Movie> movieDict;

    private HashMap<String, Show> showDict;

    private Writer fileWriter;
    private JSONArray arrayResult;

    public Repository(Input input, Writer fileWriter, JSONArray arrayResult) {
        this.actorsData = input.getActors();

        this.userDict = new HashMap<String, User>();
        for (UserInputData userData : input.getUsers()) {
            this.userDict.put(userData.getUsername(), new User(userData));
        }

        this.commandsData = input.getCommands();

        this.movieDict = new HashMap<String, Movie>();
        for (MovieInputData movieData : input.getMovies()) {
            this.movieDict.put(movieData.getTitle(), new Movie(movieData));
        }

        this.showDict = new HashMap<String, Show>();
        for (SerialInputData showData : input.getSerials()) {
            this.showDict.put(showData.getTitle(), new Show(showData));
        }

        this.fileWriter = fileWriter;
        this.arrayResult = arrayResult;
    }

    private void runCommands(ActionInputData action) throws IOException {
        if (action.getType().equals(Constants.FAVORITE)) {
            User user = this.userDict.get(action.getUsername());
            if (user.getHistory().containsKey(action.getTitle())) {
                if (user.getFavoriteMovies().contains(action.getTitle())) {
                    JSONObject data = this.fileWriter.writeFile(action.getActionId(), "", "error -> " + action.getTitle() + " is already in favourite list");
                    this.arrayResult.add(data);
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
            User user = this.userDict.get(action.getUsername());
            if (user.getHistory().containsKey(action.getTitle())) {
                int numViews = user.getHistory().get(action.getTitle());
                user.getHistory().put(action.getTitle(), numViews + 1);
            } else {
                user.getHistory().put(action.getTitle(), 1);
            }
            JSONObject data = this.fileWriter.writeFile(action.getActionId(), "", "success -> " + action.getTitle() + " was viewed with total views of " + user.getHistory().get(action.getTitle()));
            this.arrayResult.add(data);

        } else if (action.getType().equals(Constants.RATING)) {
            User user = this.userDict.get(action.getUsername());
            if (user.getHistory().containsKey(action.getTitle())){
                user.incrementNumRatings();
                if (this.movieDict.containsKey(action.getTitle())) {
                    this.movieDict.get(action.getTitle()).addRating(action.getGrade());
                    JSONObject data = this.fileWriter.writeFile(action.getActionId(), "", "success -> " + action.getTitle() + " was rated with " + action.getGrade() + " by " + action.getUsername());
                    this.arrayResult.add(data);

                } else if (this.showDict.containsKey(action.getTitle())) {
                    this.showDict.get(action.getTitle()).addRating(action.getGrade(), action.getSeasonNumber());
                    JSONObject data = this.fileWriter.writeFile(action.getActionId(), "", "success -> " + action.getTitle() + " was rated with " + action.getGrade() + " by " + action.getUsername());
                    this.arrayResult.add(data);
                }
            } else {
                JSONObject data = this.fileWriter.writeFile(action.getActionId(), "", "error -> " + action.getTitle() + " is not seen");
                this.arrayResult.add(data);
            }
        }
    }

    private void runQueries(ActionInputData action) throws IOException {
        if (action.getObjectType().equals(Constants.ACTORS)) {

        } else if (action.getObjectType().equals(Constants.MOVIES)) {
            ArrayList<Movie> moviesFiltered = Movie.findMovies(this.movieDict, action.getFilters());
            if (action.getCriteria().equals(Constants.RATINGS)) {
                if (moviesFiltered.isEmpty()) {
                    JSONObject data = this.fileWriter.writeFile(action.getActionId(), "", "Query result: []");
                    this.arrayResult.add(data);
                } else {
                    Movie.sortRating(action.getSortType(), moviesFiltered);
                    String stringList = Movie.parseQuery(moviesFiltered, action.getNumber());
                    JSONObject data = this.fileWriter.writeFile(action.getActionId(), "", "Query result: " + stringList);
                    this.arrayResult.add(data);
                }
            } else if (action.getCriteria().equals(Constants.LONGEST)) {
                if (moviesFiltered.isEmpty()) {
                    JSONObject data = this.fileWriter.writeFile(action.getActionId(), "", "Query result: []");
                    this.arrayResult.add(data);
                } else {
                    Movie.sortLongest(action.getSortType(), moviesFiltered);
                    String stringList = Movie.parseQuery(moviesFiltered, action.getNumber());
                    JSONObject data = this.fileWriter.writeFile(action.getActionId(), "", "Query result: " + stringList);
                    this.arrayResult.add(data);
                }
            }

        } else if (action.getObjectType().equals(Constants.SHOWS)) {
            ArrayList<Show> showsFiltered = Show.findShows(this.showDict, action.getFilters());
            if (action.getCriteria().equals(Constants.RATINGS)) {
                if (showsFiltered.isEmpty()) {
                    JSONObject data = this.fileWriter.writeFile(action.getActionId(), "", "Query result: []");
                    this.arrayResult.add(data);
                } else {
                    Show.sortRating(action.getSortType(), showsFiltered);
                    String stringList = Show.parseQuery(showsFiltered, action.getNumber());
                    JSONObject data = this.fileWriter.writeFile(action.getActionId(), "", "Query result: " + stringList);
                    this.arrayResult.add(data);
                }
            } else if (action.getCriteria().equals(Constants.LONGEST)) {
                if (showsFiltered.isEmpty()) {
                    JSONObject data = this.fileWriter.writeFile(action.getActionId(), "", "Query result: []");
                    this.arrayResult.add(data);
                } else {
                    Show.sortLongest(action.getSortType(), showsFiltered);
                    String stringList = Show.parseQuery(showsFiltered, action.getNumber());
                    JSONObject data = this.fileWriter.writeFile(action.getActionId(), "", "Query result: " + stringList);
                    this.arrayResult.add(data);
                }
            }

        } else if (action.getObjectType().equals(Constants.USERS)) {
            if (action.getCriteria().equals(Constants.NUM_RATINGS)) {
                String stringList = User.getUsersQuery(this.userDict, action.getSortType(), action.getNumber());
                JSONObject data = this.fileWriter.writeFile(action.getActionId(), "", "Query result: " + stringList);
                this.arrayResult.add(data);
            }
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
