package user;

import fileio.UserInputData;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Clasa care defineste un utilizator
 */
public final class User {
    private final String username;
    private final String type;
    /**
     * Hashmap cu toate videoclipurile pe care le-a vizionat utilizatorul, fiecare
     * asociat cu numarul de vizionari
     */
    private final Map<String, Integer> history;
    /**
     * Lista de videoclipuri favorite a utilizatorului
     */
    private final List<String> favoriteVideos;

    /**
     * Fiecare utilizator are un hashmap pentru serialele vazute, pentru
     * a se putea sti ce sezoane a vazut
     */
    private final Map<String, List<Integer>> ratedSerials;
    /**
     * Utilizatorul mai are si o lista cu filmele vazute
     */
    private final List<String> ratedMovies;

    public User(final UserInputData userInput) {
        ratedSerials = new HashMap<>();
        ratedMovies = new ArrayList<>();

        username = userInput.getUsername();
        type = userInput.getSubscriptionType();
        history = userInput.getHistory();
        favoriteVideos = userInput.getFavoriteMovies();
    }

    public String getUsername() {
        return username;
    }

    public String getSubscriptionType() {
        return type;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public List<String> getFavoriteVideos() {
        return favoriteVideos;
    }

    /**
     * Verifica daca lista de favorite a utilizatorului contine si videoclipul specificat
     * @param title Titlul videoclipului care se va cauta in lista
     * @return True daca videoclipul este in lista
     */
    public boolean hasFavorite(final String title) {
        return favoriteVideos.contains(title);
    }

    /**
     * Adauga un videoclip in lista de favorite a utilizatorului
     * @param title Titlul videoclipului care va fi adaugat
     * @return True daca videoclipul a fost adaugat, false daca era deja in lista
     */
    public boolean favorite(final String title) {
        if (hasFavorite(title)) {
            return false;
        }
        if (history.containsKey(title)) {
            favoriteVideos.add(title);
            return true;
        }
        return false;
    }

    /**
     * Verifica daca videoclipul a fost vizionat
     * @param title Titlul videoclipului
     * @return True daca videoclipul a fost vizionat de catre utilizator
     */
    public boolean hasViewed(final String title) {
        return history.containsKey(title);
    }

    /**
     * Vizioneaza un videoclip
     * @param title Titlul videoclipului care va fi vizionat
     * @return Numarul de vizionari ale videoclipului de catre utilizator
     */
    public int view(final String title) {
        if (history.containsKey(title)) {
            history.replace(title, history.get(title) + 1);
        } else {
            history.put(title, 1);
        }
        return history.get(title);
    }

    /**
     * Verifica daca filmul a primit rating de la utilizator
     * @param title Titlul filmului
     * @return True daca utilizatorul a dat rate filmului
     */
    public boolean hasRated(final String title) {
        return ratedMovies.contains(title);
    }

    /**
     * Verifica daca utilizatorul a dat rating sezonului unui serial
     * @param title Titlul serialului
     * @param seasonNumber Numarul sezonului
     * @return True daca a dat rate acelui sezon, false altfel
     */
    public boolean hasRatedSerialSeason(final String title, final int seasonNumber) {
        if (ratedSerials.containsKey(title)) {
            List<Integer> ratedSeasons = ratedSerials.get(title);
            return ratedSeasons.contains(seasonNumber);
        }
        return false;
    }

    /**
     * Da un rating unui sezon al unui serial
     * @param title Titlul serialului
     * @param seasonNumber Numarul sezonului
     * @return True daca rating-ul s-a dat cu succes, false daca utilizatorul a mai dat deja
     * rate acelui sezon sau daca nu a vizionat tot serialul
     */
    public boolean rateSerialSeason(final String title, final int seasonNumber) {
        if (!hasViewed(title)) {
            return false;
        }
        if (!ratedSerials.containsKey(title)) {
            // Daca acel utilizatorul nu a mai dat rating-uri acelui serial
            ArrayList<Integer> list = new ArrayList<>();
            list.add(seasonNumber);
            ratedSerials.put(title, list);
        } else {
            // Daca utilizatorul a mai dat rating-uri unor sezoane a acelui serial
            List<Integer> ratedSeasons = ratedSerials.get(title);
            if (ratedSeasons.contains(seasonNumber)) {
                // Utilizatorul a dat deja rate a celui sezon
                return false;
            }
            ratedSeasons.add(seasonNumber);
        }
        return true;
    }

    /**
     * Da un rating unui film
     * @param title Titlul filmului
     * @return True daca rating-ul a fost dat cu succes, false daca s-a mai dat deja
     * rating inainte sau daca utilizatorul nu a vizionat filmul
     */
    public boolean rateMovie(final String title) {
        if (!hasViewed(title)) {
            return false;
        }
        if (ratedMovies.contains(title)) {
            return false;
        }
        ratedMovies.add(title);
        return true;
    }

    /**
     * Calculeaza media rating-urilor pe care le-a dat utilizatorul
     * @return Media rating-urilor pe care le-a dat utilizatorul
     */
    public int getTotalRatings() {
        int totalRatings = 0;
        for (Map.Entry<String, List<Integer>> entry : ratedSerials.entrySet()) {
            totalRatings += entry.getValue().size();
        }
        totalRatings += ratedMovies.size();
        return totalRatings;
    }
}
