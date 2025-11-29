import java.util.ArrayList;

/**
 * Watchlist类管理用户的观看列表
 * 使用ArrayList存储电影ID
 */
public class Watchlist {
    private ArrayList<String> movieIds;

    /**
     * 构造函数
     */
    public Watchlist() {
        this.movieIds = new ArrayList<>();
    }

    /**
     * 从字符串初始化观看列表（从CSV文件加载时使用）
     * @param watchlistString 格式：M001;M002;M003
     */
    public Watchlist(String watchlistString) {
        this.movieIds = new ArrayList<>();
        if (watchlistString != null && !watchlistString.trim().isEmpty()) {
            String[] ids = watchlistString.split(";");
            for (String id : ids) {
                if (id != null && !id.trim().isEmpty()) {
                    this.movieIds.add(id.trim());
                }
            }
        }
    }

    /**
     * 添加电影到观看列表
     * @param movieId 电影ID
     * @return 如果成功添加返回true，如果已存在返回false
     */
    public boolean addMovie(String movieId) {
        if (movieId == null || movieId.trim().isEmpty()) {
            return false;
        }
        if (!movieIds.contains(movieId.trim())) {
            movieIds.add(movieId.trim());
            return true;
        }
        return false;
    }

    /**
     * 从观看列表移除电影
     * @param movieId 电影ID
     * @return 如果成功移除返回true，如果不存在返回false
     */
    public boolean removeMovie(String movieId) {
        if (movieId == null || movieId.trim().isEmpty()) {
            return false;
        }
        return movieIds.remove(movieId.trim());
    }

    /**
     * 检查电影是否在观看列表中
     * @param movieId 电影ID
     * @return 如果存在返回true，否则返回false
     */
    public boolean contains(String movieId) {
        if (movieId == null) {
            return false;
        }
        return movieIds.contains(movieId.trim());
    }

    /**
     * 获取观看列表中的所有电影ID
     * @return 电影ID列表
     */
    public ArrayList<String> getMovieIds() {
        return new ArrayList<>(movieIds); // 返回副本以保护封装
    }

    /**
     * 获取观看列表大小
     * @return 观看列表中的电影数量
     */
    public int size() {
        return movieIds.size();
    }

    /**
     * 检查观看列表是否为空
     * @return 如果为空返回true，否则返回false
     */
    public boolean isEmpty() {
        return movieIds.isEmpty();
    }

    /**
     * 将观看列表转换为字符串（保存到CSV文件时使用）
     * @return 格式：M001;M002;M003
     */
    @Override
    public String toString() {
        if (movieIds.isEmpty()) {
            return "";
        }
        String result = "";
        for (int i = 0; i < movieIds.size(); i++) {
            result = result + movieIds.get(i);
            if (i < movieIds.size() - 1) {
                result = result + ";";
            }
        }
        return result;
    }
}

