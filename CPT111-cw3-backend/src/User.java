/**
 * User 类：包含所有用户的通用行为
 */
public class User {
    private final String username;
    private String passwordHash;
    private final Watchlist watchlist;
    private final History history;
    private String role;

    public User(String username, String passwordValue, String roleValue, Watchlist watchlist, History history) {
        this.username = username;
        this.passwordHash = PasswordUtil.ensureHashed(passwordValue);
        if (watchlist == null) {
            this.watchlist = new Watchlist();
        } else {
            this.watchlist = watchlist;
        }
        if (history == null) {
            this.history = new History();
        } else {
            this.history = history;
        }
        this.role = UserRole.fromString(roleValue);
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPassword(String newPassword) {
        this.passwordHash = PasswordUtil.hash(newPassword);
    }

    public boolean verifyPassword(String rawPassword) {
        return PasswordUtil.verify(rawPassword, passwordHash);
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public History getHistory() {
        return history;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String roleValue) {
        this.role = UserRole.fromString(roleValue);
    }

    /**
     * Basic 用户的默认推荐数量限制，可由子类覆盖
     */
    public int getRecommendationLimit() {
        if (UserRole.isPremium(role)) {
            return 10;
        }
        return 5;
    }

    /**
     * Basic 用户默认 Watchlist 没有严格限制，可由子类覆盖
     */
    public int getWatchlistLimit() {
        if (UserRole.isPremium(role)) {
            return Integer.MAX_VALUE;
        }
        return 10;
    }

    public boolean canAddToWatchlist() {
        return watchlist.size() < getWatchlistLimit();
    }
}