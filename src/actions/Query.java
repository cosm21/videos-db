package actions;

import fileio.ActionInputData;

import java.util.List;

/**
 * Clasa abstracta care reprezinta un query si mosteneste clasa Action
 *
 * Va fi mostenita mai departe de alte clase care specifica obiectul asupra
 * caruia se efectueaza un query si care vor implementa operatiile specifice
 */
public abstract class Query extends Action {
    /**
     * Numarul de obiecte care se vor afisa
     */
    protected final int number;
    /**
     * Ordinea in care se vor afisa elementel, crescator sau descrescator
     */
    protected final String sortType;
    protected final String criteria;
    /**
     * Contine filtrele care se pot aplica query-ului
     */
    protected final List<List<String>> filters;

    protected final int year;
    protected final String genre;

    public Query(final ActionInputData input) {
        super(input);

        number = input.getNumber();
        sortType = input.getSortType();
        criteria = input.getCriteria();
        filters = input.getFilters();

        year = getYearFromFilters();
        genre = getGenreFromFilters();
    }

    /**
     * Extrage anul din lista de filtre
     * @return Anul
     */
    private int getYearFromFilters() {
        if (filters.get(0) == null) {
            return 0;
        }
        if (filters.get(0).get(0) == null) {
            return 0;
        }
        return Integer.parseInt(filters.get(0).get(0));
    }

    /**
     * Extrage genul din lista de filtre
     * @return Genul
     */
    private String getGenreFromFilters() {
        if (filters.get(1) == null) {
            return null;
        }
        if (filters.get(1).get(0) == null) {
            return null;
        }
        return filters.get(1).get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String runAction();
}
