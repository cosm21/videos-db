package actions;

import fileio.ActionInputData;
import repository.Repository;
import entertainment.Video;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import common.Constants;

/**
 * Clasa contine informatiile si metodele necesare rularii unui query asupra unor videoclipuri
 *
 * Subclasele definesc daca videoclipurile sunt filme sau seriale
 *
 * Indiferent ca se ruleaza asupra unui film sau serial, operatiile care sunt definite pentru
 * videoclipuri sunt aceleasi si de aceea sunt implementate aici
 */
public abstract class QueryVideo extends Query {
    /**
     * Intoarce o lista formata din toate videoclipurile din baza de date
     *
     * Prin toate videoclipurile se intelege acele videoclipuri care fac parte din tipul pe care
     * query-ul efectueaza actiunea, adica, clasele care mostenesc trebuie sa intoarca lista de
     * filme sau lista de seriale din baza de date
     *
     * @return Lista de videoclipuri
     */
    protected abstract List<Video> getVideoList();

    public QueryVideo(final ActionInputData actionInput) {
        super(actionInput);
    }

    /**
     * Ruleaza query-ul de rating, asa cum este descris in tema
     * @return Rezultatul query-ului
     */
    private String rating() {
        StringBuilder result = new StringBuilder(Constants.QUERY_RESULT + ": [");
        List<Video> orderedVideos = getVideoList();

        // Se elimina videoclipurile care au rating 0
        orderedVideos = orderedVideos.stream()
                .filter(video -> video.ratingsAverage() != 0)
                .collect(Collectors.toList());

        // Se elimina videoclipurile care nu au genul corespunzator
        if (genre != null) {
            orderedVideos = orderedVideos.stream()
                    .filter(video -> video.getGenres().contains(genre))
                    .collect(Collectors.toList());
        }

        // se elimina videoclipurile care nu au anul corespunzator
        if (year > 0) {
            orderedVideos = orderedVideos.stream()
                    .filter(video -> video.getYear() == year)
                    .collect(Collectors.toList());
        }

        // Se sorteaza videoclipurile
        orderedVideos.sort(new Comparator<Video>() {
            @Override
            public int compare(final Video o1, final Video o2) {
                int diff = (int) Math.floor(o1.ratingsAverage() - o2.ratingsAverage());
                if (diff == 0) {
                    return o1.getTitle().compareTo(o2.getTitle());
                }
                return diff;
            }
        });

        // Se inverseaza ordinea daca se cere
        if (sortType.equals(Constants.DESCENDING)) {
            Collections.reverse(orderedVideos);
        }

        // Daca se cere se extrag doar primele n videoclipuri
        if (number != 0) {
            orderedVideos = orderedVideos.stream()
                    .limit(number)
                    .collect(Collectors.toList());
        }

        // Se scriu titlurile videoclipurilor care au ramas in rezultat
        int i = 0;
        for (Video video : orderedVideos) {
            result.append(video.getTitle());
            if (i != orderedVideos.size() - 1) {
                result.append(", ");
            }
            i++;
        }
        result.append("]");

        return result.toString();
    }

    /**
     * Ruleaza query-ul de favorite
     * @return Rezultatul query-ului
     */
    private String favorite() {
        StringBuilder result = new StringBuilder(Constants.QUERY_RESULT + ": [");
        Repository repo = Repository.getRepo();
        List<Video> orderedVideos = getVideoList();

        // Se elimina videoclipurile care nu apar in lista de favorite a nici-unui utilizator
        orderedVideos = orderedVideos.stream()
                .filter(video -> repo.videoTotalFavorites(video.getTitle()) != 0)
                .collect(Collectors.toList());

        // Se elimina videoclipurile care nu au genul corespunzator
        if (genre != null) {
            orderedVideos = orderedVideos.stream()
                    .filter(video -> video.getGenres().contains(genre))
                    .collect(Collectors.toList());
        }

        // Se elimina video-urile care nu au anul corespunzator
        if (year > 0) {
            orderedVideos = orderedVideos.stream()
                    .filter(video -> video.getYear() == year)
                    .collect(Collectors.toList());
        }

        // Se sorteaza dupa numarul de favorite-uri
        orderedVideos.sort(new Comparator<Video>() {
            @Override
            public int compare(final Video o1, final Video o2) {
                int diff = repo.videoTotalFavorites(o1.getTitle())
                           - repo.videoTotalFavorites(o2.getTitle());
                if (diff == 0) {
                    return o1.getTitle().compareTo(o2.getTitle());
                }
                return diff;
            }
        });

        // Se inverseaza lista
        if (sortType.equals(Constants.DESCENDING)) {
            Collections.reverse(orderedVideos);
        }

        // Se pastreaza doar primele n video-uri
        if (number != 0) {
            orderedVideos = orderedVideos.stream().limit(number).collect(Collectors.toList());
        }

        // Se scriu videoclipurile ramase in rezultat
        int i = 0;
        for (Video video : orderedVideos) {
            result.append(video.getTitle());
            if (i != orderedVideos.size() - 1) {
                result.append(", ");
            }
            i++;
        }
        result.append("]");

        return result.toString();
    }

    /**
     * Ruleaza query-ul pentru cel mai lung video
     * @return Rezultatul query-ului
     */
    private String longest() {
        StringBuilder result = new StringBuilder(Constants.QUERY_RESULT + ": [");
        List<Video> orderedVideos = getVideoList();

        // Se elimina video-urile care nu au genul corespunzator
        if (genre != null) {
            orderedVideos = orderedVideos.stream()
                    .filter(video -> video.getGenres().contains(genre))
                    .collect(Collectors.toList());
        }

        // Se elimina video-urile care nu au anul corespunzator
        if (year > 0) {
            orderedVideos = orderedVideos.stream()
                    .filter(video -> video.getYear() == year)
                    .collect(Collectors.toList());
        }

        // Se sorteaza dupa durata
        orderedVideos.sort(new Comparator<Video>() {
            @Override
            public int compare(final Video o1, final Video o2) {
                int diff = o1.totalDuration() - o2.totalDuration();
                if (diff == 0) {
                    return o1.getTitle().compareTo(o2.getTitle());
                }
                return diff;
            }
        });
        // Se schimba ordinea
        if (sortType.equals(Constants.DESCENDING)) {
            Collections.reverse(orderedVideos);
        }

        // Se pastreaza primele n intrari
        if (number != 0) {
            orderedVideos = orderedVideos.stream().limit(number).collect(Collectors.toList());
        }

        int i = 0;
        for (Video video : orderedVideos) {
            result.append(video.getTitle());
            if (i != orderedVideos.size() - 1) {
                result.append(", ");
            }
            i++;
        }
        result.append("]");

        return result.toString();
    }

    /**
     * Se ruleaza query-ul pentru cel mai vizionat videoclip
     * @return Rezultatul query-ului
     */
    private String mostViewed() {
        StringBuilder result = new StringBuilder(Constants.QUERY_RESULT + ": [");
        Repository repo = Repository.getRepo();
        List<Video> orderedVideos = getVideoList();

        // Se elimina video-urile care nu au vizionari
        orderedVideos = orderedVideos.stream()
                .filter(video -> repo.videoTotalViews(video.getTitle()) != 0)
                .collect(Collectors.toList());

        // Se elimina video-urile care nu au genul corespunzator
        if (genre != null) {
            orderedVideos = orderedVideos.stream()
                    .filter(video -> video.getGenres().contains(genre))
                    .collect(Collectors.toList());
        }

        /// Acelasi lucru pentru an
        if (year > 0) {
            orderedVideos = orderedVideos.stream()
                    .filter(video -> video.getYear() == year)
                    .collect(Collectors.toList());
        }

        // Se sorteaza dupa vizionari
        orderedVideos.sort(new Comparator<Video>() {
            @Override
            public int compare(final Video o1, final Video o2) {
                int diff = repo.videoTotalViews(o1.getTitle())
                           - repo.videoTotalViews(o2.getTitle());
                if (diff == 0) {
                    return o1.getTitle().compareTo(o2.getTitle());
                }
                return diff;
            }
        });

        // Se inverseaza ordinea daca se cere
        if (sortType.equals(Constants.DESCENDING)) {
            Collections.reverse(orderedVideos);
        }

        // se pastreaza primele n intrari
        if (number != 0) {
            orderedVideos = orderedVideos.stream().limit(number).collect(Collectors.toList());
        }

        int i = 0;
        for (Video video : orderedVideos) {
            result.append(video.getTitle());
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
        return switch (criteria) {
            case Constants.RATINGS -> rating();
            case Constants.FAVORITE -> favorite();
            case Constants.LONGEST -> longest();
            case Constants.MOST_VIEWED -> mostViewed();
            default -> Constants.OPERATION_NOT_DEFINED + getClass();
        };
    }
}
