package entertainment;

import fileio.SerialInputData;

import java.util.List;

/**
 * Clasa care defineste un serial
 */
public final class Serial extends Video {
    /**
     * Serialul mai contine in plus numarul de sezoane
     */
    private final int numSeasons;
    /**
     * Si o lista de sezoane
     */
    private final List<Season> seasons;

    public Serial(final SerialInputData input) {
        super(input);

        this.seasons = input.getSeasons();
        this.numSeasons = this.seasons.size();
    }

    public int getNumSeasons() {
        return numSeasons;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    /**
     * Da un rating unui sezon a serialului
     * @param seasonNumber Numarul sezonului caruia i se va da rating
     * @param grade Rating-ul
     * @return True daca sezonul exista, false altfel
     */
    public boolean rateSeason(final int seasonNumber, final double grade) {
        if (seasonNumber < 1 || seasonNumber > numSeasons) {
            return false;
        }
        seasons.get(seasonNumber - 1).getRatings().add(grade);
        return true;
    }

    /**
     * Calculeaza media aritmetica a rating-urilor sezoanelor serialului
     * @return Media rating-urilor serialului
     */
    public double ratingsAverage() {
        double average = 0, seasonAverage = 0;
        int size = 0;
        for (Season season : seasons) {
            seasonAverage = 0;
            size++;

            if (season.getRatings().size() == 0) {
                continue;
            }

            for (double rating : season.getRatings()) {
                seasonAverage += rating;
            }

            seasonAverage /= season.getRatings().size();
            average += seasonAverage;
        }
        if (size == 0) {
            return 0;
        }
        average /= size;
        return average;
    }

    /**
     * Calculeaza suma duratelor sezoanelor serialului
     * @return Durata totala a serialului
     */
    public int totalDuration() {
        int totalDuration = 0;
        for (Season season : seasons) {
            totalDuration += season.getDuration();
        }
        return totalDuration;
    }
}
