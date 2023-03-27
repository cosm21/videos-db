package repository;

import org.json.simple.JSONArray;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import fileio.Input;
import fileio.ActorInputData;
import fileio.UserInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;
import fileio.ActionInputData;
import fileio.Writer;

import common.Constants;

import actions.Action;
import actions.Command;
import actions.QueryUser;
import actions.QueryMovie;
import actions.QueryActor;
import actions.QuerySerial;
import actions.Recommendation;

import user.User;

import actor.Actor;

import entertainment.Video;
import entertainment.Movie;
import entertainment.Serial;

/**
 * Clasa singleton care reprezinta baza de date
 * Stocheaza informatii despre actori, utilizatori, filme, seriale
 * si actiunile care se vor executa asupra acestora
 */
public final class Repository {
    private static final Repository INSTANCE = new Repository();

    private static List<Actor> actors;
    private static List<User> users;
    private static List<Movie> movies;
    private static List<Serial> serials;

    /**
     * O lista cu toate actiunile care se executa asupra bazei de date
     * Vezi lantul de mosteniri incepand cu clasa Action
     */
    private static List<Action> actions;

    public static Repository getRepo() {
        return Repository.INSTANCE;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public List<Serial> getSerials() {
        return serials;
    }

    private Repository() {
    }

    /**
     * Initializeaza toti membrii clasei
     * @param input Un obiect care contine toate datele citite dintr-un fisier
     */
    public static void initRepo(final Input input) {
        actors = new ArrayList<>();
        for (ActorInputData actorInput : input.getActors()) {
            actors.add(new Actor(actorInput));
        }

        users = new ArrayList<>();
        for (UserInputData userInput : input.getUsers()) {
            users.add(new User(userInput));
        }

        movies = new ArrayList<>();
        for (MovieInputData movieInput : input.getMovies()) {
            movies.add(new Movie(movieInput));
        }

        serials = new ArrayList<>();
        for (SerialInputData serialInput : input.getSerials()) {
            serials.add(new Serial(serialInput));
        }

        loadActions(input);
    }

    /**
     * Metoda care incarca toate actiunile in clasa, este apelata
     * ca parte din initializare
     * @param input Obiectul care contine datele citite din fisier
     */
    private static void loadActions(final Input input) {
        actions = new ArrayList<>();
        for (ActionInputData actionInput : input.getCommands()) {
            /*
             *  In functie de tipul actiunii care se citeste se instantiaza un obiect din clasa
             *  corespunzatoare acelui tip de actiune
             */
            switch (actionInput.getActionType()) {
                case Constants.COMMAND:
                    actions.add(new Command(actionInput));
                    break;
                case Constants.QUERY:
                    switch (actionInput.getObjectType()) {
                        case Constants.ACTORS:
                            actions.add(new QueryActor(actionInput));
                            break;
                        case Constants.MOVIES:
                            actions.add(new QueryMovie(actionInput));
                            break;
                        case Constants.SHOWS:
                            actions.add(new QuerySerial(actionInput));
                            break;
                        case Constants.USERS:
                            actions.add(new QueryUser(actionInput));
                            break;
                        default:
                            break;
                    }
                    break;
                case Constants.RECOMMENDATION:
                    actions.add(new Recommendation(actionInput));
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Ruleaza toate actiunile
     * @return un array cu rezultatul fiecarei actiuni, in ordinea in care
     * acele actiuni au fost date de clasa de citire a actiunilor
     */
    public List<String> runActions() {
        List<String> results = new ArrayList<>();
        for (Action action : actions) {
            results.add(action.runAction());
        }

        return results;
    }

    /**
     * Scrie rezultatele actiunilor
     * @param writer Writer-ul care va scrie rezultatele actiunilor intr-un fisier
     * @param jsonResults Array-ul in care se vor scrie rezultatele actiunilor
     */
    @SuppressWarnings("unchecked")
    public void runActionsToJSON(final Writer writer, final JSONArray jsonResults)
            throws IOException {
        List<String> results = runActions();

        for (int i = 0; i < actions.size(); ++i) {
            jsonResults.add(writer.writeFile(i + 1, null, results.get(i)));
        }
    }

    /**
     * Intoarce un utilizator din baza de date
     * @param name Numele utilizatorului
     * @return Un obiect de tip utilizator cu numele specificat, null daca nu exista
     */
    public User getUser(final String name) {
        for (User user : users) {
            if (user.getUsername().equals(name)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Intoarce filmul cu titlul specificat
     * @param title Titlul filmului care se cauta
     * @return Un obiect din clasa Movie care are titlul dat
     */
    public Movie getMovie(final String title) {
        for (Movie movie : movies) {
            if (movie.getTitle().equals(title)) {
                return movie;
            }
        }
        return null;
    }

    /**
     * Intoarce serialului cu titlul specificat
     * @param title Titlul serialului care se cauta
     * @return Un obiect din clasa Serial care are titlul dat
     */
    public Serial getSerial(final String title) {
        for (Serial serial : serials) {
            if (serial.getTitle().equals(title)) {
                return serial;
            }
        }
        return null;
    }

    /**
     * Intoarce video-ul cu titlul specificat,
     * indiferent daca este film sau serial
     * @param title Titlul video-ului care se cauta
     * @return Un obiect care mosteneste clasa Video si care are titlul specificat,
     * in acest caz poate fi din clasa Movie sau din clasa Serial, sau null
     * daca nu exista
     */
    public Video getVideo(final String title) {
        if (isMovie(title)) {
            return getMovie(title);
        }
        return getSerial(title);
    }

    /**
     * Verifica daca titlul dat este titlul unui serial din baza de date
     * @param title Titlul serialului
     * @return True daca titlul dat este un serial in baza de date, false altfel
     */
    public boolean isSerial(final String title) {
        return getSerial(title) != null;
    }

    /**
     * Verifica daca titlul dat este titlul unui film din baza de date
     * @param title Titlul filmului
     * @return True daca titlul dat este un film in baza de date, false altfel
     */
    public boolean isMovie(final String title) {
        return getMovie(title) != null;
    }

    /**
     * Calculeaza rating-ul pentru actorul specificat
     * @param actor Actorul pentru care se doreste calcularea rating-ului,
     *              rating-ul e setat in actor
     */
    private void calcActorRating(final Actor actor) {
        double average = 0;
        double rating;
        int size = 0;
        for (String videoTitle : actor.getFilmography()) {
            // Filmografia actorilor nu este neaparat prezenta in baza de date
            Video video = getVideo(videoTitle);
            if (video == null) {
                continue;
            }
            rating = video.ratingsAverage();
            if (rating == 0) {
                continue;
            }
            average += rating;
            size++;
        }
        average /= size;
        if (size == 0) {
            average = 0;
        }

        //Rating-ul calculat este stocat in actor
        actor.setRating(average);
    }

    /**
     * Calculeaza rating-ul pentru toti actorii din baza de date
     */
    public void calcActorRatings() {
        for (Actor actor : actors) {
            calcActorRating(actor);
        }
    }

    /**
     * Intoarce de cate ori apare un video in lista de favorite a utilizatorilor
     * @param title Titlul videoclipului pentru care se face calculul
     * @return De cate ori apare videoclipul in lista de favorite
     */
    public int videoTotalFavorites(final String title) {
        int totalFavorites = 0;
        for (User user : users) {
            if (user.hasFavorite(title)) {
                totalFavorites++;
            }
        }
        return totalFavorites;
    }

    /**
     * Intoarce de cate ori a fost vizualizat un videoclip
     * @param title Titlul videoclipurilor
     * @return Numarul de vizionari a acelui videoclip
     */
    public int videoTotalViews(final String title) {
        int totalViews = 0;
        for (User user : users) {
            if (user.getHistory().containsKey(title)) {
                totalViews += user.getHistory().get(title);
            }
        }
        return totalViews;
    }
}
