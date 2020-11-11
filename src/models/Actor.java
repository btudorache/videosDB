package models;

import actor.ActorsAwards;
import common.Constants;
import fileio.ActionInputData;
import fileio.ActorInputData;

import java.util.*;
import java.util.regex.Matcher;

public class Actor {
    /**
     * actor name
     */
    private String name;
    /**
     * description of the actor's career
     */
    private String careerDescription;
    /**
     * videos starring actor
     */
    private ArrayList<String> filmography;
    /**
     * awards won by the actor
     */
    private HashMap<String, Movie> filmographyDict;

    private Map<ActorsAwards, Integer> awards;

    public Actor(ActorInputData actorData) {
        this.name = actorData.getName();
        this.careerDescription = actorData.getCareerDescription();
        this.filmography = actorData.getFilmography();
        this.awards = actorData.getAwards();

        this.filmographyDict = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getCareerDescription() {
        return careerDescription;
    }

    public ArrayList<String> getFilmography() {
        return filmography;
    }

    public Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    public Double getFilmographyRatingMean(HashMap<String, Video> movieDict, HashMap<String, Video> showDict) {
        double mean = 0;
        int numVideosInDatabase = 0;
        for (String video : filmography) {
            if (movieDict.containsKey(video) && movieDict.get(video).getRating() != 0) {
                mean += movieDict.get(video).getRating();
                numVideosInDatabase++;
            } else if (showDict.containsKey(video) && showDict.get(video).getRating() != 0) {
                mean += showDict.get(video).getRating();
                numVideosInDatabase++;
            }
        }
        if (mean == 0) {
            return 0.0;
        }

        return mean / numVideosInDatabase;
    }

    public static String actorsQueryAverage(HashMap<String, Actor> actorDict, HashMap<String, Video> movieDict, HashMap<String, Video> showDict, ActionInputData action) {
        ArrayList<Actor> actorList = new ArrayList<>();
        for (Actor actor : actorDict.values()) {
            if (actor.getFilmographyRatingMean(movieDict, showDict) != 0) {
                actorList.add(actor);
            }
        }

        if (actorList.isEmpty()) {
            return "Query result: []";
        } else {
            Comparator<Actor> averageActorComparator = new Comparator<Actor>() {
                @Override
                public int compare(Actor actor1, Actor actor2) {
                    if (Double.compare(actor1.getFilmographyRatingMean(movieDict, showDict), actor2.getFilmographyRatingMean(movieDict, showDict)) == 0) {
                        return actor1.getName().compareTo(actor2.getName());
                    } else {
                        return Double.compare(actor1.getFilmographyRatingMean(movieDict, showDict), actor2.getFilmographyRatingMean(movieDict, showDict));
                    }
                }
            };

            if (action.getSortType().equals(Constants.ASCENDING)) {
                actorList.sort(averageActorComparator);
            } else if (action.getSortType().equals(Constants.DESCENDING)) {
                actorList.sort(Collections.reverseOrder(averageActorComparator));
            }

            StringBuilder builder = new StringBuilder();
            builder.append("Query result: [");
            int numQueries = Math.min(actorList.size(), action.getNumber());
            for (int i = 0; i < numQueries - 1; i++) {
                builder.append(actorList.get(i).getName());
                builder.append(", ");
            }
            builder.append(actorList.get(numQueries - 1).getName());
            builder.append(']');

            return builder.toString();
        }
    }

    @Override
    public String toString() {
        return "ActorInputData{"
                + "name='" + name + '\''
                + ", careerDescription='"
                + careerDescription + '\''
                + ", filmography=" + filmography + '}';
    }
}
