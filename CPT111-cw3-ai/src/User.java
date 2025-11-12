/**
 * User类表示一个用户
 * 包含用户名、密码、观看列表和观看历史
 */
public class User {
    private String username;
    private String password;
    private Watchlist watchlist;
    private History history;

    /**
     * 构造函数
     * @param username 用户名
     * @param password 密码
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.watchlist = new Watchlist();
        this.history = new History();
    }

    /**
     * 从CSV数据构造User对象
     * @param username 用户名
     * @param password 密码
     * @param watchlistString 观看列表字符串（格式：M001;M002）
     * @param historyString 观看历史字符串（格式：M001@2025-07-12）
     */
    public User(String username, String password, String watchlistString, String historyString) {
        this.username = username;
        this.password = password;
        this.watchlist = new Watchlist(watchlistString);
        this.history = new History(historyString);
    }

    // Getter和Setter方法
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public History getHistory() {
        return history;
    }

    /**
     * 验证密码
     * @param password 要验证的密码
     * @return 如果密码匹配返回true，否则返回false
     */
    public boolean verifyPassword(String password) {
        return this.password.equals(password);
    }

    /**
     * 检查两个User对象是否相等（基于用户名）
     * @param obj 要比较的对象
     * @return 如果用户名相同返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username != null && username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }
}

