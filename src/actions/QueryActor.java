package actions;

import actor.Actor;
import common.Constants;
import fileio.ActionInputData;
import repository.Repository;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementeaza query-urile care se pot face asupra unor actori
 */
public final class QueryActor extends Query {
    /**
     * Pentru query-ul de tip Filter Description se da si o lista de cuvinte
     */
    private final List<String> words;
    /**
     * Premiile pe care trebuie sa le aiba un actor in query-ul de tip Awards
     */
    private final List<String> awards;

    public QueryActor(final ActionInputData input) {
        super(input);

        words = getWordsFromFilters();
        awards = getAwardsFromFilters();
    }

    private List<String> getWordsFromFilters() {
        final int wordsPosition = 2;
        if (filters.get(wordsPosition) == null) {
            return null;
        }
        return filters.get(wordsPosition);
    }

    protected List<String> getAwardsFromFilters() {
        final int awardsPosition = 3;
        if (filters.get(awardsPosition) == null) {
            return null;
        }
        return filters.get(awardsPosition);
    }

    /**
     * Implementeaza query-ul de tip Average
     * @return Rezultatul query-ului
     */
    private String average() {
        StringBuilder result = new StringBuilder(Constants.QUERY_RESULT + ": [");
        Repository repo = Repository.getRepo();
        List<Actor> orderedActors = new ArrayList<>(repo.getActors());

        // Se calculeaza intai toate rating-urile pentru fiecare actor
        // Baza de date se actualizeaza in permanenta, de aceea, calculul se face abia acum
        repo.calcActorRatings();

        // Se elimina actorii care au rating 0, practic actorii a caror
        // filmografie nu apare in baza de date, sau a caror filme nu au primit rating
        orderedActors = orderedActors.stream()
                .filter(actor -> actor.getRating() != 0)
                .collect(Collectors.toList());

        // Se sorteaza dupa rating
        orderedActors.sort(new Comparator<>() {
            @Override
            public int compare(final Actor o1, final Actor o2) {
                int diff = Double.compare(o1.getRating(), o2.getRating());
                if (diff == 0) {
                    return o1.getName().compareTo(o2.getName());
                }
                return diff;
            }
        });

        // Se inverseaza
        if (sortType.equals(Constants.DESCENDING)) {
            Collections.reverse(orderedActors);
        }

        // Se opresc primii n actori
        if (number != 0) {
            orderedActors = orderedActors.stream()
                    .limit(number)
                    .collect(Collectors.toList());
        }

        // Si se scriu
        int i = 0;
        for (Actor actor : orderedActors) {
            result.append(actor.getName());
            if (i != orderedActors.size() - 1) {
                result.append(", ");
            }
            i++;
        }
        result.append("]");

        return result.toString();
    }

    /**
     * Query-ul de tip Awards asupra unor actori
     * @return Rezultatul query-ului
     */
    private String awards() {
        StringBuilder result = new StringBuilder(Constants.QUERY_RESULT + ": [");
        Repository repo = Repository.getRepo();
        List<Actor> orderedActors = new ArrayList<>(repo.getActors());

        // Pastrez doar actorii care au premiile specificate, daca nu se specifica
        // premii atunci ii consider pe toti
        if (awards != null) {
            orderedActors = orderedActors.stream()
                    .filter(actor -> actor.hasAllAwards(awards))
                    .collect(Collectors.toList());
        }

        // Ii sortez pe baza de premii si nume
        orderedActors.sort(new Comparator<>() {
            @Override
            public int compare(final Actor o1, final Actor o2) {
                int diff = o1.totalAwards() - o2.totalAwards();
                if (diff == 0) {
                    return o1.getName().compareTo(o2.getName());
                }
                return diff;
            }
        });

        // Se inverseaza lista daca se cere
        if (sortType.equals(Constants.DESCENDING)) {
            Collections.reverse(orderedActors);
        }

        // Se extrag doar primii n actori
        if (number != 0) {
            orderedActors = orderedActors.stream()
                    .limit(number)
                    .collect(Collectors.toList());
        }

        int i = 0;
        for (Actor actor : orderedActors) {
            result.append(actor.getName());
            if (i != orderedActors.size() - 1) {
                result.append(", ");
            }
            i++;
        }
        result.append("]");

        return result.toString();
    }

    /**
     * Query-ul de tip Filter Description
     * @return Rezultatul query-ului
     */
    private String filterDescription() {
        StringBuilder result = new StringBuilder(Constants.QUERY_RESULT + ": [");
        Repository repo = Repository.getRepo();
        List<Actor> orderedActors = new ArrayList<>(repo.getActors());

        // Se pastreaza doar actorii care au cuvintele specificate
        if (words != null) {
            orderedActors = orderedActors.stream().filter(new Predicate<Actor>() {
                @Override
                public boolean test(final Actor actor) {
                    for (String word : words) {
                        // Pentru acest tip de query se cauta actorii care
                        // au totate cuvintele specificate in descriere
                        // Este nevoie de un regex pentru ca cautarea
                        // de de subsiruri ar duce la rezultate incorecte
                        // "\\b" reprezinta "word boundary", adica inceputul si
                        // sfarsitul unui cuvant
                        String regex = "\\b" + word + "\\b";
                        String lowerCaseDescription = actor.getCareerDescription().toLowerCase();
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(lowerCaseDescription);
                        if (!matcher.find()) {
                            return false;
                        }
                    }
                    return true;
                }
            }).collect(Collectors.toList());
        }

        // Se sorteaza alfabetic
        orderedActors.sort(new Comparator<Actor>() {
            @Override
            public int compare(final Actor o1, final Actor o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        // Se inverseaza daca se cere
        if (sortType.equals(Constants.DESCENDING)) {
            Collections.reverse(orderedActors);
        }

        // Se extrag primii n actori
        if (number != 0) {
            orderedActors = orderedActors.stream()
                    .limit(number)
                    .collect(Collectors.toList());
        }

        int i = 0;
        for (Actor actor : orderedActors) {
            result.append(actor.getName());
            if (i != orderedActors.size() - 1) {
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
        return switch (criteria) {
            case Constants.AVERAGE -> average();
            case Constants.AWARDS -> awards();
            case Constants.FILTER_DESCRIPTIONS -> filterDescription();
            default -> Constants.OPERATION_NOT_DEFINED + getClass();
        };
    }
}
