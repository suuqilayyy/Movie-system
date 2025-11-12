import java.util.ArrayList;

/**
 * 用户类，包含用户名、密码、观影清单和历史
 */
public class User {
    private String username;                   // 用户名
    private String password;                   // 用户密码（明文，后续可加密）
    private ArrayList<String> watchlist;       // 待观看电影ID列表
    private ArrayList<HistoryItem> history;    // 观影历史

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.watchlist = new ArrayList<>();
        this.history = new ArrayList<>();
    }

    // getter/setter
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public void setPassword(String newPassword) { this.password = newPassword; }
    public ArrayList<String> getWatchlist() { return watchlist; }
    public ArrayList<HistoryItem> getHistory() { return history; }

    // 添加到待看清单
    public void addMovieToWatchlist(String movieId) {
        if (!watchlist.contains(movieId)) {
            watchlist.add(movieId);
        }
    }

    // 从待看清单移除
    public void removeMovieFromWatchlist(String movieId) {
        watchlist.remove(movieId);
    }

    // 添加观影历史
    public void addHistory(String movieId, String date) {
        history.add(new HistoryItem(movieId, date));
    }
    
    @Override
    public String toString() {
        return username;
    }
}
