import java.util.*;

/**
 * 推荐引擎类，提供基于偏好的电影推荐功能
 */
public class RecommendationEngine {
    private MovieManager movieManager;

    public RecommendationEngine(MovieManager movieManager) {
        this.movieManager = movieManager;
    }

    /**
     * 推荐算法：统计用户历史最多的电影类型，然后按评分推荐最高的 N 部同类作品
     * @param user 用户对象
     * @param N 推荐数量
     * @return 推荐的电影列表
     */
    public List<Movie> recommendByGenre(User user, int N) {
        // 统计历史最多的genre
        HashMap<String, Integer> genreCnt = new HashMap<>();
        for (HistoryItem hi : user.getHistory()) {
            Movie m = movieManager.getMovieById(hi.getMovieId());
            if (m != null) {
                genreCnt.put(m.getGenre(), genreCnt.getOrDefault(m.getGenre(), 0) + 1);
            }
        }
        String favoriteGenre = null;
        int maxCnt = 0;
        for (String g : genreCnt.keySet()) {
            if (genreCnt.get(g) > maxCnt) {
                maxCnt = genreCnt.get(g);
                favoriteGenre = g;
            }
        }
        // 没有历史，则推荐评分最高的
        List<Movie> all = movieManager.getAllMovies();
        List<Movie> list = new ArrayList<>();
        for (Movie m : all) {
            if (favoriteGenre == null || m.getGenre().equalsIgnoreCase(favoriteGenre)) {
                list.add(m);
            }
        }
        // 按评分排序
        list.sort((a, b) -> Double.compare(b.getRating(), a.getRating()));
        // 只取前N个
        if (list.size() > N) {
            return list.subList(0, N);
        } else {
            return list;
        }
    }

    /**
     * Strategy 2: Recommend top-rated movies released after or in a given year.
     */
    public List<Movie> recommendTopRatedAfterYear(int yearInclusive, int N) {
        ArrayList<Movie> all = movieManager.getAllMovies();
        ArrayList<Movie> list = new ArrayList<>();
        for (Movie m : all) {
            if (m.getYear() >= yearInclusive) {
                list.add(m);
            }
        }
        list.sort((a, b) -> Double.compare(b.getRating(), a.getRating()));
        if (list.size() > N) return list.subList(0, N);
        return list;
    }

    /**
     * Strategy 3: Recommend movies with rating >= minRating, sorted by rating desc.
     */
    public List<Movie> recommendByMinRating(double minRating, int N) {
        ArrayList<Movie> all = movieManager.getAllMovies();
        ArrayList<Movie> list = new ArrayList<>();
        for (Movie m : all) {
            if (m.getRating() >= minRating) {
                list.add(m);
            }
        }
        list.sort((a, b) -> Double.compare(b.getRating(), a.getRating()));
        if (list.size() > N) return list.subList(0, N);
        return list;
    }
}
