package models;

import actor.ActorsAwards;
import common.Constants;
import fileio.ActionInputData;
import fileio.ActorInputData;
import models.video.Video;

import java.util.*;

/**
 * Actor class used to model actor data
 */
public final class Actor {
    private final String name;
    private final String careerDescription;
    private final ArrayList<String> filmography;
    private final Map<String, Integer> awards;

    public Actor(final ActorInputData actorData) {
        this.name = actorData.getName();
        this.careerDescription = actorData.getCareerDescription();
        this.filmography = actorData.getFilmography();
        this.awards = new HashMap<>();
        for (ActorsAwards actorAward : actorData.getAwards().keySet()) {
            this.awards.put(actorAward.toString(), actorData.getAwards().get(actorAward));
        }

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

    public Map<String, Integer> getAwards() {
        return awards;
    }

    /**
     * Gets number of awards of an actor
     * @return number of awards
     */
    public int getNumAwards() {
        int numAwards = 0;
        for (int numAward : this.getAwards().values()) {
            numAwards += numAward;
        }
        return numAwards;
    }

    /**
     * Gets filmography rating mean of the actor
     * @param videoDict map containing all the movies
     * @return mean of the actor
     */
    public Double getFilmographyRatingMean(final HashMap<String, Video> videoDict) {
        double mean = 0;
        int numVideosInDatabase = 0;
        for (String video : filmography) {
            if (videoDict.containsKey(video) && videoDict.get(video).getRating() != 0) {
                mean += videoDict.get(video).getRating();
                numVideosInDatabase++;
            }
        }
        if (mean == 0) {
            return 0.0;
        }

        return mean / numVideosInDatabase;
    }

    /**
     * parses and actor list to string
     * @param actorList list of actors
     * @param numberOfElements number of actor to be parsed
     * @return string of actors
     */
    private static String parseActorList(final ArrayList<Actor> actorList,
                                         final int numberOfElements) {
        StringBuilder builder = new StringBuilder();
        builder.append("Query result: [");
        int numQueries = Math.min(actorList.size(), numberOfElements);
        for (int i = 0; i < numQueries - 1; i++) {
            builder.append(actorList.get(i).getName());
            builder.append(", ");
        }
        builder.append(actorList.get(numQueries - 1).getName());
        builder.append(']');

        return builder.toString();
    }

    /**
     * function that sorts a given list using a specific comparator and specific order
     * @param order order in which the list to be sorted (ascending or descending)
     * @param actorList actor list to be sorded
     * @param comparator comparator using for sorting
     */
    private static void sortByOrder(final ArrayList<Actor> actorList,
                                    final String order,
                                    final Comparator<Actor> comparator) {
        if (order.equals(Constants.ASCENDING)) {
            actorList.sort(comparator);
        } else if (order.equals(Constants.DESCENDING)) {
            actorList.sort(Collections.reverseOrder(comparator));
        }
    }

    /**
     * query actors by filmography mean
     * @param actorDict map of all actors
     * @param videoDict map of all videos
     * @param action action data
     * @return result string of query
     */
    public static String queryAverage(final HashMap<String, Actor> actorDict,
                                      final HashMap<String, Video> videoDict,
                                      final ActionInputData action) {
        ArrayList<Actor> actorList = new ArrayList<>();
        for (Actor actor : actorDict.values()) {
            if (actor.getFilmographyRatingMean(videoDict) != 0) {
                actorList.add(actor);
            }
        }

        if (actorList.isEmpty()) {
            return "Query result: []";
        } else {
            // comparator used for sorting
            Comparator<Actor> averageActorComparator = (actor1, actor2) -> {
                if (Double.compare(actor1.getFilmographyRatingMean(videoDict),
                                   actor2.getFilmographyRatingMean(videoDict)) == 0) {
                    return actor1.getName().compareTo(actor2.getName());
                } else {
                    return Double.compare(actor1.getFilmographyRatingMean(videoDict),
                                          actor2.getFilmographyRatingMean(videoDict));
                }
            };

            sortByOrder(actorList, action.getSortType(), averageActorComparator);
            return parseActorList(actorList, action.getNumber());
        }
    }

    /**
     * query actors by filter description
     * @param actorDict map of all actors
     * @param action action data
     * @return result string of query
     */
    public static String queryFilterDescriptions(final HashMap<String, Actor> actorDict,
                                                 final ActionInputData action) {
        ArrayList<Actor> actorList = new ArrayList<>();

        for (Actor actor : actorDict.values()) {
            // getting all the words in the description
            String replacedString = actor.getCareerDescription().replaceAll("[!?,.\"()'-]", " ");
            String[] words = replacedString.toLowerCase().split("\\s+");
            HashSet<String> wordsSet = new HashSet<>(Arrays.asList(words));
            if (wordsSet.containsAll(action.getFilters().get(2))) {
                actorList.add(actor);
            }
        }

        if (actorList.isEmpty()) {
            return "Query result: []";
        } else {
            // comparator used for sorting
            Comparator<Actor> compareByName = (actor1, actor2) ->
                    actor1.getName().compareTo(actor2.getName());
            sortByOrder(actorList, action.getSortType(), compareByName);
            return parseActorList(actorList, action.getNumber());
        }
    }

    /**
     * query actors by awards
     * @param actorDict map of all actors
     * @param action action data
     * @return result string of query
     */
    public static String queryAwards(final HashMap<String, Actor> actorDict,
                                     final ActionInputData action) {
        ArrayList<Actor> actorList = new ArrayList<>();
        for (Actor actor : actorDict.values()) {
            if (actor.getAwards().keySet().
                    containsAll(action.getFilters().get(Constants.FILTER_WORDS))) {
                actorList.add(actor);
            }
        }

        if (actorList.isEmpty()) {
            return "Query result: []";
        } else {
            Comparator<Actor> compareByNumAwards = (actor1, actor2) -> {
                if (actor1.getNumAwards() - actor2.getNumAwards() == 0) {
                    return actor1.getName().compareTo(actor2.getName());
                } else {
                    return actor1.getNumAwards() - actor2.getNumAwards();
                }
            };

            sortByOrder(actorList, action.getSortType(), compareByNumAwards);
            return parseActorList(actorList, action.getNumber());
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
