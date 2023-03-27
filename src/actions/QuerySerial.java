package actions;

import entertainment.Video;
import fileio.ActionInputData;
import repository.Repository;

import java.util.ArrayList;
import java.util.List;

public final class QuerySerial extends QueryVideo {
    public QuerySerial(final ActionInputData actionInput) {
        super(actionInput);
    }

    /**
     * Metoda care intoarce lista de seriale din baza de date
     *
     * Rularea efectiva a query-urilor se face in QueryVideo deoarece query-urile sunt identice
     * si pentru filme
     * @return Lista de seriale din baza de date
     */
    @Override
    protected List<Video> getVideoList() {
        Repository repo = Repository.getRepo();
        return new ArrayList<>(repo.getSerials());
    }
}
