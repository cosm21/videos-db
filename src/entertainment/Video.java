package entertainment;

import java.util.List;

import fileio.ShowInput;

/**
 * Clasa abstracta care reprezinta un videoclip
 */
public abstract class Video {
    private final String title;
    private final int year;
    /**
     * Actorii care participa in realizarea videoclipului
     */
    private final List<String> cast;
    /**
     * Toate genurile din care face parte videoclipul
     */
    private final List<String> genres;

    public Video(final ShowInput input) {
        this.title = input.getTitle();
        this.year = input.getYear();
        this.cast = input.getCast();
        this.genres = input.getGenres();
    }

    public final String getTitle() {
        return title;
    }

    public final int getYear() {
        return year;
    }

    public final List<String> getCast() {
        return cast;
    }

    public final List<String> getGenres() {
        return genres;
    }

    /**
     * Metoda care intoarce rating-ul videoclipului
     *
     * Fiecare copil al clasei Video trebuie sa implementeze un mod
     * de a cacula media rating-urilor care i s-au dat si trebuie sa includa
     * si un mod de a stoca rating-urile
     *
     * @return Media rating-urilor acelui videoclip
     */
    public abstract double ratingsAverage();

    /**
     * Durata totala a a videoclipului
     * @return Durata
     *
     * Copii trebuie sa implementeze un mod de a stoca durata si de a o intoarce
     */
    public abstract int totalDuration();
}
