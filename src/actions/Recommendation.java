package actions;

import common.Constants;
import entertainment.Movie;
import entertainment.Serial;
import entertainment.Video;
import fileio.ActionInputData;
import repository.Repository;
import user.User;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Clasa care extinde o actiune si implementeaza operatiile specifice
 * unei recomandari
 */
public final class Recommendation extends Action {
    /**
     * Recomandarea este facuta pentru un utilizator
     */
    private final String username;
    /**
     * Recomandarea Search mai are nevoie si de un gen al videoclipului
     */
    private final String genre;

    public Recommendation(final ActionInputData actionInput) {
        super(actionInput);
        this.username = actionInput.getUsername();
        this.genre = actionInput.getGenre();
    }

    /**
     * Recomandarea Standard
     * @return Rezultatul recomandarii
     */
    private String standard() {
        String result = Constants.RECOMMENDATION_STANDARD + " " + Constants.RESULT + ": ";
        Repository repo = Repository.getRepo();
        User user = repo.getUser(username);

        for (Movie movie : repo.getMovies()) {
            if (!user.hasViewed(movie.getTitle())) {
                result += movie.getTitle();
                return result;
            }
        }
        for (Serial serial : repo.getSerials()) {
            if (!user.hasViewed(serial.getTitle())) {
                result += serial.getTitle();
                return result;
            }
        }

        result = Constants.RECOMMENDATION_STANDARD + " " + Constants.CANNOT_BE_APPLIED;
        return result;
    }

    /**
     * Recomandarea Best Unseen
     * @return Rezultatul recomandarii
     */
    private String bestUnseen() {
        String result = Constants.RECOMMENDATION_BEST_RATED_UNSEEN + " " + Constants.RESULT + ": ";
        Repository repo = Repository.getRepo();
        List<Video> orderedVideos = new ArrayList<>();
        User user = repo.getUser(username);

        // Se adauga toate videoclipurile intr-o lista
        orderedVideos.addAll(repo.getMovies());
        orderedVideos.addAll(repo.getSerials());

        // Cele vizionate se elimina
        orderedVideos = orderedVideos.stream()
                .filter(video -> !user.hasViewed(video.getTitle()))
                .collect(Collectors.toList());

        // Se sorteaza descrescator in functie de rating
        orderedVideos.sort(new Comparator<Video>() {
            @Override
            public int compare(final Video o1, final Video o2) {
                return (int) Math.floor(o2.ratingsAverage() - o1.ratingsAverage());
            }
        });

        // Daca mai raman videoclipuri nevizionate in lista,
        // se extrage cel cu rating-ul cel mai mare
        if (orderedVideos.size() == 0) {
            result = Constants.RECOMMENDATION_BEST_RATED_UNSEEN + " " + Constants.CANNOT_BE_APPLIED;
        } else {
            result += orderedVideos.get(0).getTitle();
        }

        return result;
    }

    /**
     * Recomandare Popular
     * @return Rezultatul recomandarii
     */
    private String popular() {
        String result = Constants.RECOMMENDATION_POPULAR + " " + Constants.RESULT + ": ";
        Repository repo = Repository.getRepo();
        User user = repo.getUser(username);
        Map<String, Integer> genreViews = new HashMap<>();

        if (!user.getSubscriptionType().equals(Constants.USER_PREMIUM)) {
            result = Constants.RECOMMENDATION_POPULAR + " " + Constants.CANNOT_BE_APPLIED;
            return result;
        }

        // Se pun toate videoclipurile intr-o lista
        List<Video> allVideos = new ArrayList<>();
        allVideos.addAll(repo.getMovies());
        allVideos.addAll(repo.getSerials());

        // Pentru fiecare gen se va stoca numarul de vizualizari intr-un hashmap
        for (Video video : allVideos) {
            for (String videoGenre : video.getGenres()) {
                if (!genreViews.containsKey(videoGenre)) {
                    genreViews.put(videoGenre, repo.videoTotalViews(video.getTitle()));
                } else {
                    genreViews.put(videoGenre, genreViews.get(videoGenre)
                                          + repo.videoTotalViews(video.getTitle()));
                }
            }
        }

        // Hashmap-ul este convertit intr-o lista
        List<Map.Entry<String, Integer>> orderedGenres = new ArrayList<>(genreViews.entrySet());

        // care se sorteaza descrescator
        orderedGenres.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(final Map.Entry<String, Integer> o1,
                               final Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        // Gasesc primul videoclip nevizualizat din cel mai popular gen posibil
        for (Map.Entry<String, Integer> entry : orderedGenres) {
            for (Video video : allVideos) {
                if (!user.hasViewed(video.getTitle())
                        && video.getGenres().contains(entry.getKey())) {
                    result += video.getTitle();
                    return result;
                }
            }
        }

        result = Constants.RECOMMENDATION_POPULAR + " " + Constants.CANNOT_BE_APPLIED;
        return result;
    }

    /**
     * Recomandarea Favorite
     * @return Rezultatul recomandarii
     */
    private String favorite() {
        String result = Constants.RECOMMENDATION_FAVORITE + " " + Constants.RESULT + ": ";
        Repository repo = Repository.getRepo();
        User user = repo.getUser(username);
        Map<String, Integer> videoFavorites = new HashMap<>();
        List<Video> allVideos = new ArrayList<>();

        if (!user.getSubscriptionType().equals(Constants.USER_PREMIUM)) {
            result = Constants.FAVORITE + " " + Constants.CANNOT_BE_APPLIED;
            return result;
        }

        // Videoclipurile care se afla in lista de favorite a utilizatorilor vor
        // adaugate intr-un hashmap care retine numele videoclipului si de cate
        // ori se afla in lista de favorite a utilizatorilor
        for (User userIter : repo.getUsers()) {
            for (String title : userIter.getFavoriteVideos()) {
                if (!videoFavorites.containsKey(title)) {
                    videoFavorites.put(title, 1);
                } else {
                    videoFavorites.put(title, videoFavorites.get(title) + 1);
                }
            }
        }

        // Din moment ce al doilea criteriu de sortare este aparitia videoclipurilor
        // in baza de date, voi crea o lista noua care va contine toate videoclipurile
        allVideos.addAll(repo.getMovies());
        allVideos.addAll(repo.getSerials());

        // Se vor pastra acele videoclipuri care se afla macar in lista de favorite a unui
        // utilizator
        allVideos = allVideos.stream()
                .filter(video -> videoFavorites.get(video.getTitle()) != null)
                .collect(Collectors.toList());

        // Se sorteaza videoclipurile ramase in ordinea numarul de aparitii in lista de
        // favorite, nu mai este nevoie de alt criteriu pentru ca deja vor fi
        // in ordinea din baza de date
        allVideos.sort(new Comparator<Video>() {
            @Override
            public int compare(final Video o1, final Video o2) {
                return videoFavorites.get(o2.getTitle()) - videoFavorites.get(o1.getTitle());
            }
        });

        // Gasesc primul videoclip care satisface conditiile
        for (Video video : allVideos) {
            if (!user.hasViewed(video.getTitle())) {
                result += video.getTitle();
                return result;
            }
        }

        result = Constants.RECOMMENDATION_FAVORITE + " " + Constants.CANNOT_BE_APPLIED;
        return result;
    }

    /**
     * Recomandarea Search
     * @return Rezultatul recomandarii
     */
    private String search() {
        StringBuilder result = new StringBuilder(Constants.RECOMMENDATION_SEARCH + " "
                                                  + Constants.RESULT + ": [");
        Repository repo = Repository.getRepo();
        User user = repo.getUser(username);
        List<String> orderedVideos = new ArrayList<>();

        if (!user.getSubscriptionType().equals(Constants.USER_PREMIUM)) {
            result = new StringBuilder(Constants.RECOMMENDATION_SEARCH + " "
                                        + Constants.CANNOT_BE_APPLIED);
            return result.toString();
        }

        for (Video video : repo.getMovies()) {
            orderedVideos.add(video.getTitle());
        }
        for (Video video : repo.getSerials()) {
            orderedVideos.add(video.getTitle());
        }

        // Opresc doar videoclipurile pe care utilizatorul nu le-a vazut si care
        // sunt din genul specificat
        orderedVideos = orderedVideos.stream()
                .filter(video -> !user.hasViewed(video)
                        && repo.getVideo(video).getGenres().contains(genre))
                .collect(Collectors.toList());

        // Daca nu sunt videoclipuri care sa indeplineasca criteriile atunci trebuie
        // afisat alt mesaj
        if (orderedVideos.size() == 0) {
            result = new StringBuilder(Constants.RECOMMENDATION_SEARCH + " "
                                        + Constants.CANNOT_BE_APPLIED);
            return result.toString();
        }

        // Se sorteaza videoclipurile
        orderedVideos.sort(new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                int diff = (int) Math.floor(repo.getVideo(o1).ratingsAverage()
                                            - repo.getVideo(o2).ratingsAverage());
                if (diff == 0) {
                    return o1.compareTo(o2);
                }
                return diff;
            }
        });

        // Se scriu totate videoclipurile care indeplinesc conditiile
        int i = 0;
        for (String title : orderedVideos) {
            result.append(title);
            if (i != orderedVideos.size() - 1) {
                result.append(", ");
            }
            i++;
        }
        result.append("]");

        return result.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String runAction() {
        return switch (type) {
            case Constants.STANDARD -> standard();
            case Constants.BEST_UNSEEN -> bestUnseen();
            case Constants.POPULAR -> popular();
            case Constants.FAVORITE -> favorite();
            case Constants.SEARCH -> search();
            default -> Constants.OPERATION_NOT_DEFINED + getClass();
        };
    }
}
