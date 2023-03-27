package actions;

import entertainment.Video;
import fileio.ActionInputData;
import repository.Repository;

import java.util.ArrayList;
import java.util.List;

public final class QueryMovie extends QueryVideo {
    public QueryMovie(final ActionInputData actionInput) {
        super(actionInput);
    }

    /**
     * Metoda care intoarce lista de filme din baza de date
     *
     * Rularea efectiva a query-urilor se face in QueryVideo deoarece query-urile sunt identice
     * si pentru seriale
     * @return Lista de filme din baza de date
     */
    @Override
    protected List<Video> getVideoList() {
        Repository repo = Repository.getRepo();
        return new ArrayList<>(repo.getMovies());
    }
}
