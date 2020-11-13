package database;

import common.Constants;
import fileio.*;
import models.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.*;


public final class Repository {
    private final HashMap<String, Actor> actorDict;

    private final List<ActionInputData> commandsData;

    private final HashMap<String, User> userDict;

    private final LinkedHashSet<String> videoSet;

    private final HashMap<String, Video> movieDict;

    private final HashMap<String, Video> showDict;

    private final HashMap<String, Video> videoDict;

    private final Writer fileWriter;
    private final JSONArray arrayResult;

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
                if (this.movieDict.containsKey(videoTitle)) {
                    this.movieDict.get(videoTitle).
                            addNumViews(userData.getHistory().get(videoTitle));
                } else if (this.showDict.containsKey(videoTitle)) {
                    this.showDict.get(videoTitle).
                            addNumViews(userData.getHistory().get(videoTitle));
                }
            }
        }

        this.fileWriter = fileWriter;
        this.arrayResult = arrayResult;
    }

    private void writeMessage(final int id, final String message) throws IOException {
        JSONObject data = this.fileWriter.writeFile(id, "", message);
        this.arrayResult.add(data);
    }

    private void runCommands(final ActionInputData action) throws IOException {
        User user = this.userDict.get(action.getUsername());
        if (action.getType().equals(Constants.FAVORITE)) {
            writeMessage(action.getActionId(),
                         user.commandFavorite(action, this.videoDict));
        } else if (action.getType().equals(Constants.VIEW)) {
            writeMessage(action.getActionId(),
                         user.commandView(action, this.videoDict));
        } else if (action.getType().equals(Constants.RATING)) {
            writeMessage(action.getActionId(),
                         user.commandRating(action, this.movieDict, this.videoDict));
        }
    }

    private void runQueries(final ActionInputData action) throws IOException {
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

    private void runActorQueries(final ActionInputData action) throws IOException {
        if (action.getCriteria().equals(Constants.AVERAGE)) {
            String queryString = Actor.queryAverage(actorDict, videoDict, action);
            writeMessage(action.getActionId(), queryString);
        } else if (action.getCriteria().equals(Constants.AWARDS)) {
            String queryString = Actor.queryAwards(actorDict, action);
            writeMessage(action.getActionId(), queryString);
        } else if (action.getCriteria().equals(Constants.FILTER_DESCRIPTIONS)) {
            String queryString = Actor.queryFilterDescriptions(this.actorDict, action);
            writeMessage(action.getActionId(), queryString);
        }
    }

    private void runVideoQueries(final ActionInputData action,
                                 final HashMap<String, Video> dict) throws IOException {
        ArrayList<Video> videosFiltered = Video.findShows(dict, action.getFilters());
        if (action.getCriteria().equals(Constants.RATINGS)) {
            String stringList = Video.queryRating(videosFiltered, action);
            writeMessage(action.getActionId(), stringList);
        } else if (action.getCriteria().equals(Constants.LONGEST)) {
            String stringList = Video.queryLongest(videosFiltered, action);
            writeMessage(action.getActionId(), stringList);
        } else if (action.getCriteria().equals(Constants.FAVORITE)) {
            String stringList = Video.queryFavorite(videosFiltered, action);
            writeMessage(action.getActionId(), stringList);
        } else if (action.getCriteria().equals(Constants.MOST_VIEWED)) {
            String stringList = Video.queryMostViewed(videosFiltered, action);
            writeMessage(action.getActionId(), stringList);
        }
    }

    private void runUserQueries(final ActionInputData action) throws IOException {
        if (action.getCriteria().equals(Constants.NUM_RATINGS)) {
            writeMessage(action.getActionId(),
                         User.getUsersQuery(this.userDict,
                                            action));
        }
    }

    private void runRecommendations(final ActionInputData action) throws IOException {
        User user = this.userDict.get(action.getUsername());
        if (action.getType().equals(Constants.STANDARD)) {
            writeMessage(action.getActionId(),
                         user.recommendStandard(this.videoSet, this.videoDict));
        } else if (action.getType().equals(Constants.BEST_UNSEEN)) {
            writeMessage(action.getActionId(),
                         user.recommendBestUnseen(this.videoSet, this.videoDict));
        } else if (action.getType().equals(Constants.POPULAR)) {
            writeMessage(action.getActionId(),
                         user.recommendPopular(this.videoSet, this.videoDict));
        } else if (action.getType().equals(Constants.FAVORITE)) {
            writeMessage(action.getActionId(),
                         user.recommendFavorite(this.videoSet, this.videoDict));
        } else if (action.getType().equals(Constants.SEARCH)) {
            writeMessage(action.getActionId(),
                         user.recommendSearch(this.videoSet, this.videoDict, action.getGenre()));
        }
    }

    /**
     * Main function of the class
     * @throws IOException
     */
    public void runActions() throws IOException {
        for (ActionInputData action : this.commandsData) {
            if (action.getActionType().equals(Constants.COMMAND)) {
                this.runCommands(action);
            } else if (action.getActionType().equals(Constants.QUERY)) {
                this.runQueries(action);
            } else if (action.getActionType().equals(Constants.RECOMMENDATION)) {
                this.runRecommendations(action);
            }
        }
    }
}
