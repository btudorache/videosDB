package models;

import actor.ActorsAwards;
import fileio.ActorInputData;

import java.util.ArrayList;
import java.util.Map;

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
    private Map<ActorsAwards, Integer> awards;

    public Actor(ActorInputData actorData) {
        this.name = actorData.getName();
        this.careerDescription = actorData.getCareerDescription();
        this.filmography = actorData.getFilmography();
        this.awards = actorData.getAwards();
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

    @Override
    public String toString() {
        return "ActorInputData{"
                + "name='" + name + '\''
                + ", careerDescription='"
                + careerDescription + '\''
                + ", filmography=" + filmography + '}';
    }
}
