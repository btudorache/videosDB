package database;

import common.Constants;
import fileio.*;
import models.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.*;


public class Repository {
    private HashMap<String, Actor> actorDict;
    /**
     * List of commands
     */
    private List<ActionInputData> commandsData;

    private HashMap<String, User> userDict;

    private LinkedHashSet<String> videoSet;

    private HashMap<String, Video> movieDict;

    private HashMap<String, Video> showDict;

    private Writer fileWriter;
    private JSONArray arrayResult;

    public Repository(Input input, Writer fileWriter, JSONArray arrayResult) {
        this.actorDict = new HashMap<>();
        for (ActorInputData actorData : input.getActors()) {
            this.actorDict.put(actorData.getName(), new Actor(actorData));
        }

        this.commandsData = input.getCommands();

        this.videoSet = new LinkedHashSet<>();

        this.movieDict = new HashMap<>();
        for (MovieInputData movieData : input.getMovies()) {
            this.videoSet.add(movieData.getTitle());
            this.movieDict.put(movieData.getTitle(), new Movie(movieData));
        }

        this.showDict = new HashMap<>();
        for (SerialInputData showData : input.getSerials()) {
            this.videoSet.add(showData.getTitle());
            this.showDict.put(showData.getTitle(), new Show(showData));
        }

        this.userDict = new HashMap<>();
        for (UserInputData userData : input.getUsers()) {
            this.userDict.put(userData.getUsername(), new User(userData));
            for (String favoriteVideo : userData.getFavoriteMovies()) {
                if (this.movieDict.containsKey(favoriteVideo)) {
                    this.movieDict.get(favoriteVideo).incrementNumFavorites();
                } else if (this.showDict.containsKey(favoriteVideo)) {
                    this.showDict.get(favoriteVideo).incrementNumFavorites();
                }
            }

            for (String videoTitle : userData.getHistory().keySet()) {
                if (this.movieDict.containsKey(videoTitle)) {
                    this.movieDict.get(videoTitle).addNumViews(userData.getHistory().get(videoTitle));
                } else if (this.showDict.containsKey(videoTitle)) {
                    this.showDict.get(videoTitle).addNumViews(userData.getHistory().get(videoTitle));
                }
            }
        }

        this.fileWriter = fileWriter;
        this.arrayResult = arrayResult;
    }

    private void writeMessage(int id, String field, String message) throws IOException {
        JSONObject data = this.fileWriter.writeFile(id, field, message);
        this.arrayResult.add(data);
    }

    private void runCommands(ActionInputData action) throws IOException {
        User user = this.userDict.get(action.getUsername());
        if (action.getType().equals(Constants.FAVORITE)) {
            String commandString = user.commandFavorite(action);
            writeMessage(action.getActionId(), "", commandString);
        } else if (action.getType().equals(Constants.VIEW)) {
            if (this.movieDict.containsKey(action.getTitle())) {
                this.movieDict.get(action.getTitle()).addNumViews(1);
            } else if (this.showDict.containsKey(action.getTitle())) {
                this.showDict.get(action.getTitle()).addNumViews(1);
            }

            if (user.getHistory().containsKey(action.getTitle())) {
                int numViews = user.getHistory().get(action.getTitle());
                user.getHistory().put(action.getTitle(), numViews + 1);
            } else {
                user.getHistory().put(action.getTitle(), 1);
            }
            writeMessage(action.getActionId(), "", "success -> " + action.getTitle() + " was viewed with total views of " + user.getHistory().get(action.getTitle()));

        } else if (action.getType().equals(Constants.RATING)) {
            if (user.getHistory().containsKey(action.getTitle())){
                user.incrementNumRatings();
                if (this.movieDict.containsKey(action.getTitle())) {
                    if (user.getRatedMovies().contains(action.getTitle())) {
                        writeMessage(action.getActionId(), "", "error -> " + action.getTitle() + " has been already rated" );
                    } else {
                        this.movieDict.get(action.getTitle()).addRating(action.getGrade(), 0);
                        this.movieDict.get(action.getTitle()).incrementNumFavorites();
                        user.addToRatedMovies(action.getTitle());
                        writeMessage(action.getActionId(), "", "success -> " + action.getTitle() + " was rated with " + action.getGrade() + " by " + action.getUsername());
                    }
                } else if (this.showDict.containsKey(action.getTitle())) {
                    if (user.getRatedMovies().contains(action.getTitle())) {
                        writeMessage(action.getActionId(), "", "error -> " + action.getTitle() + " has been already rated" );
                    } else {
                        this.showDict.get(action.getTitle()).addRating(action.getGrade(), action.getSeasonNumber());
                        this.showDict.get(action.getTitle()).incrementNumFavorites();
                        user.addToRatedMovies(action.getTitle());
                        writeMessage(action.getActionId(), "", "success -> " + action.getTitle() + " was rated with " + action.getGrade() + " by " + action.getUsername());
                    }
                }
            } else {
                writeMessage(action.getActionId(), "", "error -> " + action.getTitle() + " is not seen");
            }
        }
    }

    private void runQueries(ActionInputData action) throws IOException {
        if (action.getObjectType().equals(Constants.ACTORS)) {
            this.runActorQueries(action);
        } else if (action.getObjectType().equals(Constants.MOVIES)) {
            this.runVideoQueries(action, this.movieDict);
        } else if (action.getObjectType().equals(Constants.SHOWS)) {
            this.runVideoQueries(action, this.showDict);
        } else if (action.getObjectType().equals(Constants.USERS)) {
            this.runUserQueries(action);
        }
    }

    private void runActorQueries(ActionInputData action) throws IOException {
        if (action.getCriteria().equals(Constants.AVERAGE)) {
            String queryString = Actor.queryAverage(actorDict, movieDict, showDict, action);
            writeMessage(action.getActionId(), "", queryString);
        } else if (action.getCriteria().equals(Constants.AWARDS)) {
            String queryString = Actor.queryAwards(actorDict, action);
            writeMessage(action.getActionId(), "", queryString);
        } else if (action.getCriteria().equals(Constants.FILTER_DESCRIPTIONS)) {
            String queryString = Actor.queryFilterDescriptions(actorDict, action);
            writeMessage(action.getActionId(), "", queryString);
        }
    }

    private void runVideoQueries(ActionInputData action, HashMap<String, Video> videoDict) throws IOException {
        ArrayList<Video> videosFiltered = Video.findShows(videoDict, action.getFilters());
        if (action.getCriteria().equals(Constants.RATINGS)) {
            String stringList = Video.queryRating(videosFiltered, action);
            writeMessage(action.getActionId(), "", stringList);
        } else if (action.getCriteria().equals(Constants.LONGEST)) {
            String stringList = Video.queryLongest(videosFiltered, action);
            writeMessage(action.getActionId(), "", stringList);
        } else if (action.getCriteria().equals(Constants.FAVORITE)) {
            String stringList = Video.queryFavorite(videosFiltered, action);
            writeMessage(action.getActionId(), "", stringList);
        } else if (action.getCriteria().equals(Constants.MOST_VIEWED)) {
            String stringList = Video.queryMostViewed(videosFiltered, action);
            writeMessage(action.getActionId(), "", stringList);
        }
    }

    private void runUserQueries(ActionInputData action) throws IOException {
        if (action.getCriteria().equals(Constants.NUM_RATINGS)) {
            String stringList = User.getUsersQuery(this.userDict, action.getSortType(), action.getNumber());
            writeMessage(action.getActionId(), "", "Query result: " + stringList);
        }
    }

    private void runRecommendations(ActionInputData action) throws IOException {
        User user = this.userDict.get(action.getUsername());
        if (action.getType().equals(Constants.STANDARD)) {
            String stringRecommendation = user.recommendStandard(this.videoSet, this.movieDict, this.showDict);
            writeMessage(action.getActionId(), "", stringRecommendation);
        } else if (action.getType().equals(Constants.BEST_UNSEEN)) {
            String stringRecommendation = user.recommendBestUnseen(this.videoSet, this.movieDict, this.showDict);
            writeMessage(action.getActionId(), "", stringRecommendation);
        } else if (action.getType().equals(Constants.POPULAR)) {

        } else if (action.getType().equals(Constants.FAVORITE)) {
            String stringRecommendation = user.recommendFavorite(this.movieDict, this.showDict);
            writeMessage(action.getActionId(), "", stringRecommendation);
        } else if (action.getType().equals(Constants.SEARCH)) {
            String stringRecommendation = user.recommendSearch(this.videoSet, this.movieDict, this.showDict, action.getGenre());
            writeMessage(action.getActionId(), "", stringRecommendation);
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
