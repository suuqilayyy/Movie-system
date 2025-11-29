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
        
        // 获取用户的类型统计
        HashMap<String, Integer> genreCounts = user.getHistory().getGenreCounts(movies);
        
        // 找到用户观看最多的类型
        String favoriteGenre = null;
        int maxCount = 0;
        if (!genreCounts.isEmpty()) {
            for (String genre : genreCounts.keySet()) {
                int count = genreCounts.get(genre);
                if (count > maxCount) {
                    maxCount = count;
                    favoriteGenre = genre;
                }
            }
        }

        // 如果有最喜欢的类型，优先推荐该类型的电影
        if (favoriteGenre != null) {
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
                // 优先添加最喜欢的类型的电影
                if (favoriteGenre.equals(movie.getGenre())) {
                    result.add(movie);
                }
            }
        }

        // 如果还没达到推荐数量，添加其他类型的电影
        if (result.size() < limit) {
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
                // 如果不在结果中，则添加（避免重复添加最喜欢的类型）
                boolean alreadyAdded = false;
                for (Movie addedMovie : result) {
                    if (addedMovie.getId().equals(movie.getId())) {
                        alreadyAdded = true;
                        break;
                    }
                }
                if (!alreadyAdded) {
                    result.add(movie);
                }
            }
        }

        // 如果还是没有结果，忽略待看列表限制
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

        // 如果仍然没有结果，推荐所有电影
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
        return "Genre-Based Recommendation";
    }
}
