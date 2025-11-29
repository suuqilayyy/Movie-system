import java.util.ArrayList;
import java.util.HashMap;

public class RecommendationEngine {
    private final HashMap<String, Movie> movies;

    public RecommendationEngine(HashMap<String, Movie> movies) {
        this.movies = movies;
    }

    public ArrayList<Movie> getRecommendations(User user, int requestedNumber) {
        int limit = requestedNumber;
        if (limit < 1) {
            limit = 1;
        }
        int roleLimit = user.getRecommendationLimit();
        if (limit > roleLimit) {
            limit = roleLimit;
        }

        ArrayList<Movie> result = new ArrayList<>();
        for (Movie movie : movies.values()) {
            if (result.size() >= limit) {
                break;
            }
            if (movie == null) {
                continue;
            }
            if (user.getHistory().contains(movie.getId())) {
                continue;
            }
            if (user.getWatchlist().contains(movie.getId())) {
                continue;
            }
            result.add(movie);
        }

        if (result.isEmpty()) {
            for (Movie movie : movies.values()) {
                if (result.size() >= limit) {
                    break;
                }
                if (movie == null) {
                    continue;
                }
                if (user.getHistory().contains(movie.getId())) {
                    continue;
                }
                result.add(movie);
            }
        }

        if (result.isEmpty()) {
            for (Movie movie : movies.values()) {
                if (result.size() >= limit) {
                    break;
                }
                if (movie != null) {
                    result.add(movie);
                }
            }
        }
        return result;
    }

    public String getStrategyName() {
        return "Basic Recommendation";
    }
}
