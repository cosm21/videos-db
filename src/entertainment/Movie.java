package entertainment;

import fileio.MovieInputData;

import java.util.ArrayList;
import java.util.List;

/**
 * Clasa care defineste un film
 */
public final class Movie extends Video {
    /**
     * Filmul are si durata
     */
    private final int duration;
    /**
     * Rating-urile date unui film fac parte din clasa
     */
    private final List<Double> ratings;

    public Movie(final MovieInputData input) {
        super(input);
        this.ratings = new ArrayList<>();
        this.duration = input.getDuration();
    }

    public List<Double> getRatings() {
        return ratings;
    }

    public int getDuration() {
        return duration;
    }

    /**
     * Adauga un rating in lista de rating-uri
     * @param grade Rating-ul care va fi adaugat
     */
    public void rateMovie(final double grade) {
        ratings.add(grade);
    }

    /**
     * Calculeaza media rating-urilor date, practic rating-ul filmului
     * @return Media rating-urilor
     */
    @Override
    public double ratingsAverage() {
        double average = 0;
        for (double rating : ratings) {
            average += rating;
        }
        if (ratings.size() == 0) {
            return 0;
        }
        average /= ratings.size();
        return average;
    }

    /**
     * In cazul filmului, durata totala este stocata direct
     * @return Durata filmului
     */
    @Override
    public int totalDuration() {
        return getDuration();
    }
}
