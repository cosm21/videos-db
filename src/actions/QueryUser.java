package actions;

import common.Constants;
import fileio.ActionInputData;
import repository.Repository;
import user.User;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementeaza query-urile care se pot face asupra utilizatorilor
 */
public final class QueryUser extends Query {
    public QueryUser(final ActionInputData actionInput) {
        super(actionInput);
    }

    /**
     * Implementeaza query-ul de tip Number of Ratings asupra utilizatorilor
     * @return Rezultatul query-ului
     */
    public String numRatings() {
        StringBuilder result = new StringBuilder(Constants.QUERY_RESULT + ": [");
        Repository repo = Repository.getRepo();
        List<User> orderedUsers = repo.getUsers();

        // Se elimina utilizatorii care nu au dat rating-uri
        orderedUsers = orderedUsers.stream()
                .filter(user -> user.getTotalRatings() != 0)
                .collect(Collectors.toList());

        // Se sorteaza utilizatorii care raman
        orderedUsers.sort(new Comparator<User>() {
            @Override
            public int compare(final User o1, final User o2) {
                int diff = o1.getTotalRatings() - o2.getTotalRatings();
                if (diff == 0) {
                    return o1.getUsername().compareTo(o2.getUsername());
                }
                return diff;
            }
        });

        // Se inverseaza ordinea daca se cere
        if (sortType.equals(Constants.DESCENDING)) {
            Collections.reverse(orderedUsers);
        }

        // Se pastreaza primii n utilizatori daca se vrea
        if (number != 0) {
            orderedUsers = orderedUsers.stream()
                    .limit(number)
                    .collect(Collectors.toList());
        }

        // Se scrie rezultatul
        int i = 0;
        for (User user : orderedUsers) {
            result.append(user.getUsername());
            if (i != orderedUsers.size() - 1) {
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
            case Constants.NUM_RATINGS -> numRatings();
            default -> Constants.OPERATION_NOT_DEFINED + getClass();
        };
    }
}

