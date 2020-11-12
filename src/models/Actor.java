package models;

import actor.ActorsAwards;
import common.Constants;
import fileio.ActionInputData;
import fileio.ActorInputData;

import java.util.*;

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

    public int getNumAwards() {
        int numAwards = 0;
        for (int numAward : this.getAwards().values()) {
            numAwards += numAward;
        }
        return numAwards;
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

    private static String parseActorList(ArrayList<Actor> actorList, int numberOfElements) {
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

    private static void sortByOrder(ArrayList<Actor> actorList, String order, Comparator<Actor> comparator) {
        if (order.equals(Constants.ASCENDING)) {
            actorList.sort(comparator);
        } else if (order.equals(Constants.DESCENDING)) {
            actorList.sort(Collections.reverseOrder(comparator));
        }
    }

    public static String queryAverage(HashMap<String, Actor> actorDict, HashMap<String, Video> movieDict, HashMap<String, Video> showDict, ActionInputData action) {
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

            sortByOrder(actorList, action.getSortType(), averageActorComparator);
            return parseActorList(actorList, action.getNumber());
        }
    }

    public static String queryFilterDescriptions(HashMap<String, Actor> actorDict, ActionInputData action) {
        ArrayList<Actor> actorList = new ArrayList<>();
        actorLoop:
        for (Actor actor : actorDict.values()) {
            for (String keyWord : action.getFilters().get(2)) {
                if (!actor.getCareerDescription().toLowerCase().contains(keyWord)) {
                    break actorLoop;
                }
            }
            actorList.add(actor);
        }

        if (actorList.isEmpty()) {
            return "Query result: []";
        } else {
            Comparator<Actor> compareByName = new Comparator<Actor>() {
                @Override
                public int compare(Actor actor1, Actor actor2) {
                    return actor1.getName().compareTo(actor2.getName());
                }
            };

            sortByOrder(actorList, action.getSortType(), compareByName);
            return parseActorList(actorList, action.getNumber());
        }
    }

    public static String queryAwards(HashMap<String, Actor> actorDict, ActionInputData action) {
        ArrayList<Actor> actorList = new ArrayList<>();
        for (Actor actor : actorDict.values()) {
            if (actor.getAwards().keySet().containsAll(action.getFilters().get(3))) {
                actorList.add(actor);
            }
        }

        if (actorList.isEmpty()) {
            return "Query result: []";
        } else {
            Comparator<Actor> compareByNumAwards = new Comparator<Actor>() {
                @Override
                public int compare(Actor actor1, Actor actor2) {
                    if (actor1.getNumAwards() - actor2.getNumAwards() == 0) {
                        return actor1.getName().compareTo(actor2.getName());
                    } else {
                        return actor1.getNumAwards() - actor2.getNumAwards();
                    }
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
