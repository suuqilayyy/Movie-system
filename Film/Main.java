import java.util.*;
import java.io.*;
import java.time.LocalDate;

/**
 * Main entry for the Movie Recommendation & Tracker system.
 * Console-based, uses CSV files in CW3_Data_Files/data.
 */
public class Main {
    private static final String MOVIES_CSV = "CW3_Data_Files/data/movies.csv";
    private static final String USERS_CSV = "CW3_Data_Files/data/users.csv";

    private final Scanner scanner = new Scanner(System.in);
    private final MovieManager movieManager = new MovieManager();
    private final UserManager userManager = new UserManager();
    private RecommendationEngine recommendationEngine;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        // Load data
        movieManager.loadMovies(MOVIES_CSV);
        userManager.loadUsers(USERS_CSV);
        recommendationEngine = new RecommendationEngine(movieManager);

        // Main loop
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Select: ");
            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegister();
                    break;
                case 3:
                    handlePasswordChange();
                    break;
                case 4:
                    running = false;
                    break;
                default:
                    println("Invalid option.");
            }
        }

        // Save users on exit
        userManager.saveUsers(USERS_CSV);
        println("Bye!");
    }

    private void printMainMenu() {
        println("");
        println("==============================");
        println("Movie Recommender & Tracker");
        println("==============================");
        println("1) Login");
        println("2) Register (new user)");
        println("3) Change password");
        println("4) Exit");
    }

    private void handleLogin() {
        String username = readLine("Username: ");
        String password = readLine("Password: ");
        User user = userManager.login(username, password);
        if (user == null) {
            println("Login failed.");
            return;
        }
        println("Welcome, " + user.getUsername() + "!");
        userMenu(user);
    }

    private void handleRegister() {
        String username = readLine("Choose username: ");
        if (username == null || username.trim().isEmpty()) {
            println("Username cannot be empty.");
            return;
        }
        String password = readLine("Choose password: ");
        if (password == null || password.trim().isEmpty()) {
            println("Password cannot be empty.");
            return;
        }
        User u = userManager.register(username.trim(), password);
        if (u == null) {
            println("Username already exists.");
        } else {
            println("Registered successfully. You can now login.");
            userManager.saveUsers(USERS_CSV);
        }
    }

    private void handlePasswordChange() {
        String username = readLine("Username: ");
        String oldPwd = readLine("Current password: ");
        String newPwd = readLine("New password: ");
        if (newPwd == null || newPwd.trim().isEmpty()) {
            println("New password cannot be empty.");
            return;
        }
        boolean ok = userManager.changePassword(username, oldPwd, newPwd);
        if (ok) {
            println("Password changed.");
            userManager.saveUsers(USERS_CSV);
        } else {
            println("Failed to change password (wrong username or password).");
        }
    }

    private void userMenu(User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            printUserMenu(user);
            int choice = readInt("Select: ");
            switch (choice) {
                case 1:
                    browseMovies();
                    break;
                case 2:
                    addMovieToWatchlist(user);
                    break;
                case 3:
                    removeMovieFromWatchlist(user);
                    break;
                case 4:
                    viewWatchlist(user);
                    break;
                case 5:
                    markMovieWatched(user);
                    break;
                case 6:
                    viewHistory(user);
                    break;
                case 7:
                    getRecommendations(user);
                    break;
                case 8:
                    loggedIn = false;
                    break;
                default:
                    println("Invalid option.");
            }
        }
        // Optionally save after user session changes
        userManager.saveUsers(USERS_CSV);
    }

    private void printUserMenu(User user) {
        println("");
        println("---- User Menu (" + user.getUsername() + ") ----");
        println("1) Browse movies");
        println("2) Add movie to watchlist");
        println("3) Remove movie from watchlist");
        println("4) View watchlist");
        println("5) Mark movie as watched");
        println("6) View history");
        println("7) Get recommendations");
        println("8) Logout");
    }

    private void browseMovies() {
        ArrayList<Movie> all = movieManager.getAllMovies();
        if (all.isEmpty()) {
            println("No movies available.");
            return;
        }
        println("\nAll Movies (" + all.size() + "):");
        for (Movie m : all) {
            println(m.toString());
        }
    }

    private void addMovieToWatchlist(User user) {
        String id = readLine("Enter movie ID to add: ");
        Movie m = movieManager.getMovieById(id);
        if (m == null) {
            println("Movie not found.");
            return;
        }
        if (user.getWatchlist().contains(m.getId())) {
            println("Already in watchlist.");
            return;
        }
        user.addMovieToWatchlist(m.getId());
        println("Added: " + m.getTitle());
    }

    private void removeMovieFromWatchlist(User user) {
        String id = readLine("Enter movie ID to remove: ");
        if (!user.getWatchlist().contains(id)) {
            println("Not in watchlist.");
            return;
        }
        user.removeMovieFromWatchlist(id);
        println("Removed: " + id);
    }

    private void viewWatchlist(User user) {
        ArrayList<String> wl = user.getWatchlist();
        if (wl.isEmpty()) {
            println("Watchlist is empty.");
            return;
        }
        println("\nWatchlist (" + wl.size() + "):");
        for (String id : wl) {
            Movie m = movieManager.getMovieById(id);
            if (m != null) {
                println(m.toString());
            } else {
                println(id);
            }
        }
    }

    private void markMovieWatched(User user) {
        String id = readLine("Enter movie ID watched: ");
        Movie m = movieManager.getMovieById(id);
        if (m == null) {
            println("Movie not found.");
            return;
        }
        // Remove from watchlist if present
        user.removeMovieFromWatchlist(id);
        // Add to history with today date
        String today = LocalDate.now().toString();
        user.addHistory(id, today);
        println("Marked watched: " + m.getTitle() + " on " + today);
    }

    private void viewHistory(User user) {
        ArrayList<HistoryItem> his = user.getHistory();
        if (his.isEmpty()) {
            println("History is empty.");
            return;
        }
        println("\nHistory (" + his.size() + "):");
        for (HistoryItem h : his) {
            Movie m = movieManager.getMovieById(h.getMovieId());
            String title = (m != null ? m.getTitle() : h.getMovieId());
            println(h.getDate() + " - " + title + " (" + h.getMovieId() + ")");
        }
    }

    private void getRecommendations(User user) {
        println("");
        println("Choose recommendation strategy:");
        println("1) By your favorite genre (history-based)");
        println("2) Top rated after a given year");
        println("3) By minimum rating threshold");
        int strategy = readInt("Select: ");

        int n = readInt("How many recommendations? ");
        if (n <= 0) { println("Enter a positive number."); return; }

        List<Movie> recs;
        switch (strategy) {
            case 2: {
                int year = readInt("Year (inclusive): ");
                recs = recommendationEngine.recommendTopRatedAfterYear(year, n);
                break;
            }
            case 3: {
                double minRating = readDouble("Minimum rating (e.g. 8.0): ");
                recs = recommendationEngine.recommendByMinRating(minRating, n);
                break;
            }
            case 1:
            default:
                recs = recommendationEngine.recommendByGenre(user, n);
        }

        if (recs == null || recs.isEmpty()) { println("No recommendations available."); return; }
        println("\nRecommendations:");
        int i = 1;
        for (Movie m : recs) { println(i + ". " + m.toString()); i++; }
    }

    // Utility input helpers
    private String readLine(String prompt) {
        System.out.print(prompt);
        try {
            return scanner.nextLine().trim();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    private int readInt(String prompt) {
        while (true) {
            String s = readLine(prompt);
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {
                println("Please enter a valid number.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            String s = readLine(prompt);
            try {
                return Double.parseDouble(s);
            } catch (Exception e) {
                println("Please enter a valid number.");
            }
        }
    }

    private void println(String s) {
        System.out.println(s);
    }
}
