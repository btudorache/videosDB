package models;

import common.Constants;
import fileio.MovieInputData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Movie extends Video implements Comparable<Movie> {
    /**
     * Duration in minutes of a movie
     */
    private final int duration;

    private int numRatings;


    public Movie(MovieInputData movieData) {
        super(movieData.getTitle(), movieData.getYear(), movieData.getCast(), movieData.getGenres());
        this.duration = movieData.getDuration();
        this.numRatings = 0;
    }

    public static ArrayList<Movie> findMovies(HashMap<String, Movie> movies, List<List<String>> filters) {
        ArrayList<Movie> movieList = new ArrayList<Movie>();
        // if both filter present
        if (filters.get(0) != null && filters.get(1) != null) {
            for (Movie movie : movies.values()) {
                if (filters.get(0).get(0) != null &&
                    movie.getYear() == Integer.parseInt(filters.get(0).get(0)) &&
                    movie.getGenres().containsAll(filters.get(1))) {
                    movieList.add(movie);
                }
            }
        // if only year filter
        } else if (filters.get(0) != null) {
            for (Movie movie : movies.values()) {
                if (movie.getYear() == Integer.parseInt(filters.get(0).get(0))) {
                    movieList.add(movie);
                }
            }
        // if only genre filter
        } else if (filters.get(1) != null) {
            for (Movie movie : movies.values()) {
                if (movie.getGenres().containsAll(filters.get(1))) {
                    movieList.add(movie);
                }
            }
        // if no filter
        } else {
            movieList.addAll(movies.values());
        }
        return movieList;
    }

    public static String parseQuery(ArrayList<Movie> movieList) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append('[');
        for (int i = 0; i < movieList.size() - 1; i++) {
            queryBuilder.append(movieList.get(i).getTitle());
            queryBuilder.append(", ");
        }
        queryBuilder.append(movieList.get(movieList.size() - 1).getTitle());
        queryBuilder.append(']');
        return queryBuilder.toString();
    }

    public static void sortType(String order, ArrayList<Movie> movieList) {
        if (order.equals(Constants.ASCENDING)) {
            Collections.sort(movieList);
        } else if (order.equals(Constants.DESCENDING)) {
            Collections.sort(movieList, Collections.reverseOrder());
        }
    }

    public void addRating(double rate) {
        this.numRatings++;
        this.rating = this.rating * (this.numRatings - 1) / this.numRatings + rate / this.numRatings;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public int compareTo(Movie that) {
        if (Double.compare(this.getRating(), that.getRating()) == 0) {
            return this.getTitle().compareTo(that.getTitle());
        } else {
            return Double.compare(this.getRating(), that.getRating());
        }
    }

    @Override
    public String toString() {
        return "MovieInputData{" + "title= "
                + super.getTitle() + "year= "
                + super.getYear() + "duration= "
                + duration + "cast {"
                + super.getCast() + " }\n"
                + "genres {" + super.getGenres() + " }\n ";
    }
}
