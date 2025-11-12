/**
 * 观影历史条目类
 * 包含电影ID和观看日期
 */
public class HistoryItem {
    private String movieId;
    private String date; // 格式“YYYY-MM-DD”

    public HistoryItem(String movieId, String date) {
        this.movieId = movieId;
        this.date = date;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return movieId + "@" + date;
    }
}
