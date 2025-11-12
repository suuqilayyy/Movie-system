import java.util.ArrayList;
import java.util.HashMap;

/**
 * History类管理用户的观看历史
 * 存储电影ID和观看日期
 */
public class History {
    private ArrayList<HistoryEntry> entries;

    /**
     * 内部类，表示一条观看历史记录
     */
    private class HistoryEntry {
        private String movieId;
        private String date;

        public HistoryEntry(String movieId, String date) {
            this.movieId = movieId;
            this.date = date;
        }

        public String getMovieId() {
            return movieId;
        }

        public String getDate() {
            return date;
        }
    }

    /**
     * 构造函数
     */
    public History() {
        this.entries = new ArrayList<>();
    }

    /**
     * 从字符串初始化观看历史（从CSV文件加载时使用）
     * @param historyString 格式：M001@2025-07-12;M011@2025-08-10
     */
    public History(String historyString) {
        this.entries = new ArrayList<>();
        if (historyString != null && !historyString.trim().isEmpty()) {
            String[] parts = historyString.split(";");
            for (String part : parts) {
                if (part != null && !part.trim().isEmpty()) {
                    String[] entry = part.split("@");
                    if (entry.length == 2) {
                        entries.add(new HistoryEntry(entry[0].trim(), entry[1].trim()));
                    }
                }
            }
        }
    }

    /**
     * 添加电影到观看历史
     * @param movieId 电影ID
     * @param date 观看日期（格式：YYYY-MM-DD）
     * @return 如果成功添加返回true
     */
    public boolean addMovie(String movieId, String date) {
        if (movieId == null || movieId.trim().isEmpty()) {
            return false;
        }
        if (date == null || date.trim().isEmpty()) {
            return false;
        }
        // 检查是否已存在，如果存在则更新日期
        for (HistoryEntry entry : entries) {
            if (entry.getMovieId().equals(movieId.trim())) {
                entry.date = date.trim();
                return true;
            }
        }
        entries.add(new HistoryEntry(movieId.trim(), date.trim()));
        return true;
    }

    /**
     * 检查电影是否在观看历史中
     * @param movieId 电影ID
     * @return 如果存在返回true，否则返回false
     */
    public boolean contains(String movieId) {
        for (HistoryEntry entry : entries) {
            if (entry.getMovieId().equals(movieId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取观看历史中的所有电影ID
     * @return 电影ID列表
     */
    public ArrayList<String> getMovieIds() {
        ArrayList<String> ids = new ArrayList<>();
        for (HistoryEntry entry : entries) {
            ids.add(entry.getMovieId());
        }
        return ids;
    }

    /**
     * 获取电影观看日期
     * @param movieId 电影ID
     * @return 观看日期，如果不存在返回null
     */
    public String getDate(String movieId) {
        for (HistoryEntry entry : entries) {
            if (entry.getMovieId().equals(movieId)) {
                return entry.getDate();
            }
        }
        return null;
    }

    /**
     * 获取观看历史大小
     * @return 观看历史中的电影数量
     */
    public int size() {
        return entries.size();
    }

    /**
     * 检查观看历史是否为空
     * @return 如果为空返回true，否则返回false
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * 获取用户观看最多的类型
     * @param movies HashMap，键为电影ID，值为Movie对象
     * @return HashMap，键为类型，值为观看次数
     */
    public HashMap<String, Integer> getGenreCounts(HashMap<String, Movie> movies) {
        HashMap<String, Integer> genreCounts = new HashMap<>();
        for (HistoryEntry entry : entries) {
            Movie movie = movies.get(entry.getMovieId());
            if (movie != null) {
                String genre = movie.getGenre();
                genreCounts.put(genre, genreCounts.getOrDefault(genre, 0) + 1);
            }
        }
        return genreCounts;
    }

    /**
     * 将观看历史转换为字符串（保存到CSV文件时使用）
     * @return 格式：M001@2025-07-12;M011@2025-08-10
     */
    public String toString() {
        if (entries.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < entries.size(); i++) {
            HistoryEntry entry = entries.get(i);
            sb.append(entry.getMovieId()).append("@").append(entry.getDate());
            if (i < entries.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }
}

