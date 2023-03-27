package actions;

import entertainment.Movie;
import entertainment.Serial;
import fileio.ActionInputData;
import repository.Repository;
import user.User;

import common.Constants;

/**
 * Contine implementarile comenzilor, extinde Action
 */
public final class Command extends Action {
    /**
     * Comanda este executata de un utilizator
     */
    private final String username;
    /**
     * Numele viideoclipul asupra caruia se executa comanda
     */
    private final String videoTitle;
    /**
     * Rating-ul care se va da
     */
    private final double grade;
    /**
     * Numarul sezonului, daca e nevoie
     */
    private final int seasonNumber;

    public Command(final ActionInputData input) {
        super(input);

        username = input.getUsername();
        videoTitle = input.getTitle();
        grade = input.getGrade();
        seasonNumber = input.getSeasonNumber();
    }

    /**
     * Implementeaza comanda de favorite
     * @return Rezultatul comenzii
     */
    private String favorite() {
        Repository repo = Repository.getRepo();
        User user = repo.getUser(username);
        String result = "";

        if (user == null) {
            return result;
        }


        if (!user.favorite(videoTitle)) {
            result = Constants.ERROR + " -> " + videoTitle + " ";
            if (!user.hasViewed(videoTitle)) {
                result += Constants.IS_NOT_SEEN;
            } else if (user.hasFavorite(videoTitle)) {
                result += Constants.IS_ALREADY_IN_FAVORITE_LIST;
            }
        } else {
            result = Constants.SUCCESS +  " -> " + videoTitle + " "
                    + Constants.WAS_ADDED_AS_A_FAVORITE;
        }
        return  result;
    }

    /**
     * Implementeaza comanda de view
     * @return Rezultatul comenzii
     */
    private String view() {
        Repository repo = Repository.getRepo();
        User user = repo.getUser(username);
        String result = "";

        if (user == null) {
            return result;
        }

        result = Constants.SUCCESS + " -> " + videoTitle + " "
                    + Constants.WAS_VIEWED_WITH_TOTAL_VIEWS_OF + " "
                    + user.view(videoTitle);
        return  result;
    }

    /**
     * Implementeaza comanda de rating
     * @return Rezultatul comenzii
     */
    private String rating() {
        Repository repo = Repository.getRepo();
        User user = repo.getUser(username);
        String result = "";

        if (seasonNumber == 0) {
            // Daca este film
            if (!user.rateMovie(videoTitle)) {
                result = Constants.ERROR + " -> " + videoTitle + " ";
                if (!user.hasViewed(videoTitle)) {
                    // Nu a fost vazut inainte
                    result += Constants.IS_NOT_SEEN;
                } else if (user.hasRated(videoTitle)) {
                    // I s-a dat deja rate
                    result += Constants.HAS_BEEN_ALREADY_RATED;
                }
            } else {
                // A fost dat rate cu succes
                result = Constants.SUCCESS + " -> " + videoTitle + " " + Constants.WAS_RATED_WITH
                            + " " + grade + " " + Constants.BY + " " + username;
                Movie movie = repo.getMovie(videoTitle);
                movie.rateMovie(grade);
            }
        } else {
            // Daca este serial
            if (!user.rateSerialSeason(videoTitle, seasonNumber)) {
                result = Constants.ERROR + " -> " + videoTitle + " ";
                if (!user.hasViewed(videoTitle)) {
                    // Nu a fost vazut
                    result += Constants.IS_NOT_SEEN;
                } else if (user.hasRatedSerialSeason(videoTitle, seasonNumber)) {
                    // I s-a dat deja rate
                    result += Constants.HAS_BEEN_ALREADY_RATED;
                }
            } else {
                // S-a dat rate cu succes
                result = Constants.SUCCESS + " -> " + videoTitle + " " + Constants.WAS_RATED_WITH
                            + " " + grade + " " + Constants.BY + " " + username;
                Serial serial = repo.getSerial(videoTitle);
                if (!serial.rateSeason(seasonNumber, grade)) {
                    // Daca numarul sezonului nu este corect
                    result = Constants.ERROR + " -> " + Constants.SEASON_OUT_OF_BOUNDS;
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String runAction() {
        return switch (type) {
            case Constants.VIEW -> view();
            case Constants.FAVORITE -> favorite();
            case Constants.RATING -> rating();
            default -> Constants.OPERATION_NOT_DEFINED + getClass();
        };
    }
}
