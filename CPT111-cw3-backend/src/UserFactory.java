public final class UserFactory {
    private UserFactory() {}

    public static User createUser(String username, String passwordValue, String role) {
        return createUser(username, passwordValue, role, new Watchlist(), new History());
    }

    public static User createUser(String username, String passwordValue, String role, Watchlist watchlist, History history) {
        return new User(username, passwordValue, role, watchlist, history);
    }
}

