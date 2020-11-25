package database;

import common.Constants;
import fileio.*;
import models.*;
import models.video.Movie;
import models.video.Show;
import models.video.Video;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.*;


/**
 * Main database class. This class holds the data from input databases and also contains
 * the main logic of the queries.
 */
public final class Repository {
    private final List<ActionInputData> commandsData;

    private final HashMap<String, User> userDict;

    private final HashMap<String, Actor> actorDict;

    /**
     * set containing both movie and show names
     */
    private final LinkedHashSet<String> videoSet;
    /**
     * map containing movies
     */
    private final HashMap<String, Video> movieDict;
    /**
     * map containing shows
     */
    private final HashMap<String, Video> showDict;
    /**
     * map containing both movies and shows
     */
    private final HashMap<String, Video> videoDict;

    private final Writer fileWriter;
    private final JSONArray arrayResult;

    /**
     * all of the input is put into dictionaries/arrays.
     * Additional processing useful the queries is done for every model.
     * @param input inputData
     * @param fileWriter fileWriter associated with the output file
     * @param arrayResult JSON array used for writing json to output file
     */
    public Repository(final Input input, final Writer fileWriter, final JSONArray arrayResult) {
        this.actorDict = new HashMap<>();
        for (ActorInputData actorData : input.getActors()) {
            this.actorDict.put(actorData.getName(), new Actor(actorData));
        }

        this.commandsData = input.getCommands();

        this.videoSet = new LinkedHashSet<>();
        this.videoDict = new HashMap<>();

        this.movieDict = new HashMap<>();
        for (MovieInputData movieData : input.getMovies()) {
            this.videoSet.add(movieData.getTitle());
            Movie movie = new Movie(movieData);
            this.movieDict.put(movieData.getTitle(), movie);
            this.videoDict.put(movieData.getTitle(), movie);
        }

        this.showDict = new HashMap<>();
        for (SerialInputData showData : input.getSerials()) {
            this.videoSet.add(showData.getTitle());
            Show show = new Show(showData);
            this.showDict.put(showData.getTitle(), show);
            this.videoDict.put(showData.getTitle(), show);
        }

        this.userDict = new HashMap<>();
        for (UserInputData userData : input.getUsers()) {
            this.userDict.put(userData.getUsername(), new User(userData));
            for (String favoriteVideo : userData.getFavoriteMovies()) {
                if (this.videoDict.containsKey(favoriteVideo)) {
                    this.videoDict.get(favoriteVideo).incrementNumFavorites();
                }
            }

            for (String videoTitle : userData.getHistory().keySet()) {
                if (this.videoDict.containsKey(videoTitle)) {
                    this.videoDict.get(videoTitle)
                            .addNumViews(userData.getHistory().get(videoTitle));
                }
            }
        }

        this.fileWriter = fileWriter;
        this.arrayResult = arrayResult;
    }

    /**
     * function that writes parses and writes json data to output
     * @param id action of id
     * @param message message to be written to output
     * @throws IOException needed for writing json to output
     */
    private void writeMessage(final int id, final String message) throws IOException {
        JSONObject data = this.fileWriter.writeFile(id, "", message);
        this.arrayResult.add(data);
    }

    /**
     * function that runs the user commands
     * @param action data used for performing queries
     * @throws IOException needed for writing json to output
     */
    private void runCommands(final ActionInputData action) throws IOException {
        User user = this.userDict.get(action.getUsername());
        switch (action.getType()) {
            case Constants.FAVORITE -> writeMessage(action.getActionId(),
                    user.commandFavorite(action, this.videoDict));
            case Constants.VIEW -> writeMessage(action.getActionId(),
                    user.commandView(action, this.videoDict));
            case Constants.RATING -> writeMessage(action.getActionId(),
                    user.commandRating(action, this.movieDict, this.videoDict));
        }
    }

    /**
     * function that filters query types
     * @param action data used for performing queries
     * @throws IOException needed for writing json to output
     */
    private void runQueries(final ActionInputData action) throws IOException {
        switch (action.getObjectType()) {
            case Constants.ACTORS -> this.runActorQueries(action);
            case Constants.MOVIES -> this.runVideoQueries(action, this.movieDict);
            case Constants.SHOWS -> this.runVideoQueries(action, this.showDict);
            case Constants.USERS -> this.runUserQueries(action);
        }
    }

    /**
     * function that runs the actor queries
     * @param action data used for performing queries
     * @throws IOException needed for writing json to output
     */
    private void runActorQueries(final ActionInputData action) throws IOException {
        switch (action.getCriteria()) {
            case Constants.AVERAGE -> writeMessage(action.getActionId(),
                    Actor.queryAverage(this.actorDict, this.videoDict, action));
            case Constants.AWARDS -> writeMessage(action.getActionId(),
                    Actor.queryAwards(this.actorDict, action));
            case Constants.FILTER_DESCRIPTIONS -> writeMessage(action.getActionId(),
                    Actor.queryFilterDescriptions(this.actorDict, action));
        }
    }

    /**
     * function that runs the videos query.
     * @param action data used for performing queries
     * @param dict dictionary containing either movies or shows
     * @throws IOException needed for writing json to output
     */
    private void runVideoQueries(final ActionInputData action,
                                 final HashMap<String, Video> dict) throws IOException {
        ArrayList<Video> videosFiltered = Video.findShows(dict, action.getFilters());
        switch (action.getCriteria()) {
            case Constants.RATINGS -> writeMessage(action.getActionId(),
                    Video.queryRating(videosFiltered, action));
            case Constants.LONGEST -> writeMessage(action.getActionId(),
                    Video.queryLongest(videosFiltered, action));
            case Constants.FAVORITE -> writeMessage(action.getActionId(),
                    Video.queryFavorite(videosFiltered, action));
            case Constants.MOST_VIEWED -> writeMessage(action.getActionId(),
                    Video.queryMostViewed(videosFiltered, action));
        }
    }

    /**
     * function that runs the user query
     * @param action data used for performing queries
     * @throws IOException needed for writing json to output
     */
    private void runUserQueries(final ActionInputData action) throws IOException {
        if (action.getCriteria().equals(Constants.NUM_RATINGS)) {
            writeMessage(action.getActionId(),
                    User.getUsersQuery(this.userDict, action));
        }
    }

    /**
     * function that filters recommendations and runs the correct one
     * @param action data used for performing queries
     * @throws IOException needed for writing json to output
     */
    private void runRecommendations(final ActionInputData action) throws IOException {
        User user = this.userDict.get(action.getUsername());
        switch (action.getType()) {
            case Constants.STANDARD -> writeMessage(action.getActionId(),
                    user.recommendStandard(this.videoSet, this.videoDict));
            case Constants.BEST_UNSEEN -> writeMessage(action.getActionId(),
                    user.recommendBestUnseen(this.videoSet, this.videoDict));
            case Constants.POPULAR -> writeMessage(action.getActionId(),
                    user.recommendPopular(this.videoSet, this.videoDict));
            case Constants.FAVORITE -> writeMessage(action.getActionId(),
                    user.recommendFavorite(this.videoSet, this.videoDict));
            case Constants.SEARCH -> writeMessage(action.getActionId(),
                    user.recommendSearch(this.videoSet, this.videoDict, action.getGenre()));
        }
    }

    /**
     * Main function of the class.
     * Every query is filtered through the functions untill the correct function is found.
     * @throws IOException needed for writing json to output
     */
    public void runActions() throws IOException {
        for (ActionInputData action : this.commandsData) {
            switch (action.getActionType()) {
                case Constants.COMMAND -> this.runCommands(action);
                case Constants.QUERY -> this.runQueries(action);
                case Constants.RECOMMENDATION -> this.runRecommendations(action);
            }
        }
    }
}
