package actor;

import fileio.ActorInputData;

import java.util.List;
import java.util.Map;

import common.Constants;

/**
 *  Clasa care defineste un actor si care ofera functionalitati legate de award-urile pe care
 *  un actor le poate avea
 */
public final class Actor {
    private final String name;
    private final String careerDescription;
    /**
     * Filemele si serialele in care a participat actorul
     */
    private final List<String> filmography;
    /**
     * Premiile pe care le-a primit
     */
    private final Map<ActorsAwards, Integer> awards;
    /**
     * Rating-ul total asociat unui actor asa cum este definit in tema
     */
    private double rating;

    public Actor(final ActorInputData input) {
        name = input.getName();
        careerDescription = input.getCareerDescription();
        filmography = input.getFilmography();
        awards = input.getAwards();
    }

    public String getName() {
        return name;
    }

    public String getCareerDescription() {
        return careerDescription;
    }

    public List<String> getFilmography() {
        return filmography;
    }

    public Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    public void setRating(final double ratings) {
        this.rating = ratings;
    }

    public double getRating() {
        return rating;
    }

    /**
     * Calculeaza numarul total de premii pe care le are un actor
     * @return Numarul total de premii
     */
    public int totalAwards() {
        int numAwards = 0;
        for (Map.Entry<ActorsAwards, Integer> entry : awards.entrySet()) {
            numAwards += entry.getValue();
        }
        return numAwards;
    }

    private static ActorsAwards stringToAward(final String awardName) {
        return switch (awardName) {
            case Constants.AWARD_BEST_PERFORMANCE -> ActorsAwards.BEST_PERFORMANCE;
            case Constants.AWARD_BEST_DIRECTOR -> ActorsAwards.BEST_DIRECTOR;
            case Constants.AWARD_PEOPLE_CHOICE_AWARD -> ActorsAwards.PEOPLE_CHOICE_AWARD;
            case Constants.AWARD_BEST_SUPPORTING_ACTOR -> ActorsAwards.BEST_SUPPORTING_ACTOR;
            case Constants.AWARD_BEST_SCREENPLAY -> ActorsAwards.BEST_SCREENPLAY;
            default -> null;
        };
    }

    /**
     * Verifica daca actorul are award-ul specificat
     * @param award Award-ul
     * @return True daca actorul area award-ul
     */
    public boolean hasAward(final ActorsAwards award) {
        return awards.containsKey(award);
    }

    /**
     * Verifica daca actorul are toate award-urile specificate
     * @param requiredAwards Lista cu award-uri
     * @return True daca actorul area toate award-urile
     */
    public boolean hasAllAwards(final List<String> requiredAwards) {
        for (String award : requiredAwards) {
            if (!hasAward(stringToAward(award))) {
                return false;
            }
        }
        return true;
    }
}
