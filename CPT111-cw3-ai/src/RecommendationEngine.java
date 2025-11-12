import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * RecommendationEngine类实现电影推荐算法
 * 支持多种推荐策略：按类型、按评分、按年份
 */
public class RecommendationEngine {
    private HashMap<String, Movie> movies;
    private String strategy; // "genre", "rating", "year"

    /**
     * 构造函数
     * @param movies 所有电影的HashMap
     */
    public RecommendationEngine(HashMap<String, Movie> movies) {
        this.movies = movies;
        this.strategy = "genre"; // 默认策略
    }

    /**
     * 设置推荐策略
     * @param strategy 策略名称："genre", "rating", 或 "year"
     */
    public void setStrategy(String strategy) {
        if (strategy != null && (strategy.equals("genre") || 
            strategy.equals("rating") || strategy.equals("year"))) {
            this.strategy = strategy;
        }
    }

    /**
     * 获取当前推荐策略
     * @return 当前策略名称
     */
    public String getStrategy() {
        return strategy;
    }

    /**
     * 根据用户历史生成推荐
     * @param user 用户对象
     * @param n 推荐电影数量
     * @return 推荐的电影列表
     */
    public ArrayList<Movie> getRecommendations(User user, int n) {
        if (user == null || n <= 0) {
            return new ArrayList<>();
        }

        History history = user.getHistory();
        Watchlist watchlist = user.getWatchlist();

        // 获取用户已观看和已在观看列表中的电影ID
        ArrayList<String> excludedIds = new ArrayList<>();
        excludedIds.addAll(history.getMovieIds());
        excludedIds.addAll(watchlist.getMovieIds());

        ArrayList<Movie> recommendations = new ArrayList<>();

        switch (strategy) {
            case "genre":
                recommendations = getRecommendationsByGenre(history, excludedIds, n);
                break;
            case "rating":
                recommendations = getRecommendationsByRating(excludedIds, n);
                break;
            case "year":
                recommendations = getRecommendationsByYear(history, excludedIds, n);
                break;
            default:
                recommendations = getRecommendationsByGenre(history, excludedIds, n);
        }

        return recommendations;
    }

    /**
     * 基于用户观看最多的类型进行推荐
     * @param history 用户观看历史
     * @param excludedIds 要排除的电影ID列表
     * @param n 推荐数量
     * @return 推荐的电影列表
     */
    private ArrayList<Movie> getRecommendationsByGenre(History history, 
                                                      ArrayList<String> excludedIds, int n) {
        // 获取用户观看最多的类型
        HashMap<String, Integer> genreCounts = history.getGenreCounts(movies);
        
        if (genreCounts.isEmpty()) {
            // 如果用户没有观看历史，返回评分最高的电影
            return getRecommendationsByRating(excludedIds, n);
        }

        // 找到观看次数最多的类型
        String favoriteGenre = "";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : genreCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                favoriteGenre = entry.getKey();
            }
        }

        // 获取该类型的所有电影，排除已观看和已在观看列表中的
        ArrayList<Movie> candidates = new ArrayList<>();
        for (Movie movie : movies.values()) {
            if (movie.getGenre().equals(favoriteGenre) && 
                !excludedIds.contains(movie.getId())) {
                candidates.add(movie);
            }
        }

        // 按评分排序
        Collections.sort(candidates, new Comparator<Movie>() {
            @Override
            public int compare(Movie m1, Movie m2) {
                return Double.compare(m2.getRating(), m1.getRating());
            }
        });

        // 返回前N部
        ArrayList<Movie> result = new ArrayList<>();
        for (int i = 0; i < Math.min(n, candidates.size()); i++) {
            result.add(candidates.get(i));
        }

        // 如果推荐数量不足，用其他高评分电影补充
        if (result.size() < n) {
            ArrayList<Movie> additional = getRecommendationsByRating(excludedIds, n - result.size());
            for (Movie movie : additional) {
                if (!result.contains(movie)) {
                    result.add(movie);
                    if (result.size() >= n) break;
                }
            }
        }

        return result;
    }

    /**
     * 基于评分进行推荐（评分最高的电影）
     * @param excludedIds 要排除的电影ID列表
     * @param n 推荐数量
     * @return 推荐的电影列表
     */
    private ArrayList<Movie> getRecommendationsByRating(ArrayList<String> excludedIds, int n) {
        ArrayList<Movie> candidates = new ArrayList<>();
        for (Movie movie : movies.values()) {
            if (!excludedIds.contains(movie.getId())) {
                candidates.add(movie);
            }
        }

        // 按评分排序
        Collections.sort(candidates, new Comparator<Movie>() {
            @Override
            public int compare(Movie m1, Movie m2) {
                return Double.compare(m2.getRating(), m1.getRating());
            }
        });

        // 返回前N部
        ArrayList<Movie> result = new ArrayList<>();
        for (int i = 0; i < Math.min(n, candidates.size()); i++) {
            result.add(candidates.get(i));
        }
        return result;
    }

    /**
     * 基于用户观看电影的年份进行推荐（推荐相似年份的电影）
     * @param history 用户观看历史
     * @param excludedIds 要排除的电影ID列表
     * @param n 推荐数量
     * @return 推荐的电影列表
     */
    private ArrayList<Movie> getRecommendationsByYear(History history, 
                                                      ArrayList<String> excludedIds, int n) {
        // 计算用户观看电影的平均年份
        ArrayList<String> watchedIds = history.getMovieIds();
        if (watchedIds.isEmpty()) {
            return getRecommendationsByRating(excludedIds, n);
        }

        int totalYear = 0;
        int count = 0;
        for (String movieId : watchedIds) {
            Movie movie = movies.get(movieId);
            if (movie != null) {
                totalYear += movie.getYear();
                count++;
            }
        }

        if (count == 0) {
            return getRecommendationsByRating(excludedIds, n);
        }

        int averageYear = totalYear / count;

        // 获取年份接近平均年份的电影
        ArrayList<Movie> candidates = new ArrayList<>();
        for (Movie movie : movies.values()) {
            if (!excludedIds.contains(movie.getId())) {
                candidates.add(movie);
            }
        }

        // 按与平均年份的差值排序
        final int avgYear = averageYear;
        Collections.sort(candidates, new Comparator<Movie>() {
            @Override
            public int compare(Movie m1, Movie m2) {
                int diff1 = Math.abs(m1.getYear() - avgYear);
                int diff2 = Math.abs(m2.getYear() - avgYear);
                if (diff1 != diff2) {
                    return Integer.compare(diff1, diff2);
                }
                // 如果年份差值相同，按评分排序
                return Double.compare(m2.getRating(), m1.getRating());
            }
        });

        // 返回前N部
        ArrayList<Movie> result = new ArrayList<>();
        for (int i = 0; i < Math.min(n, candidates.size()); i++) {
            result.add(candidates.get(i));
        }
        return result;
    }
}

