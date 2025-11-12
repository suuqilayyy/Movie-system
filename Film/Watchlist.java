import java.util.ArrayList;

/**
 * Watchlist 类：包装用户的待观看清单
 * 内部使用 ArrayList<String> 存储电影ID
 */
public class Watchlist {
    private final ArrayList<String> movieIds;

    public Watchlist() {
        this.movieIds = new ArrayList<>();
    }

    public ArrayList<String> getAll() {
        return movieIds;
    }

    /**
     * 添加电影到清单（避免重复）
     */
    public void add(String movieId) {
        if (!movieIds.contains(movieId)) {
            movieIds.add(movieId);
        }
    }

    /**
     * 从清单移除
     */
    public void remove(String movieId) {
        movieIds.remove(movieId);
    }

    public boolean contains(String movieId) {
        return movieIds.contains(movieId);
    }
}
