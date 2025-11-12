import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Main类 - 电影推荐与跟踪系统的主程序
 * 实现命令行菜单界面和所有核心功能
 */
public class Main {
    private static HashMap<String, Movie> movies;
    private static HashMap<String, User> users;
    private static User currentUser;
    private static FileManager fileManager;
    private static RecommendationEngine recommendationEngine;
    private static Scanner scanner;

    /**
     * 主方法
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        
        // 初始化文件管理器（使用相对路径）
        String moviesPath = "CW3_Data_Files/data/movies.csv";
        String usersPath = "CW3_Data_Files/data/users.csv";
        fileManager = new FileManager(moviesPath, usersPath);

        // 加载数据
        try {
            System.out.println("Loading data...");
            movies = fileManager.loadMovies();
            users = fileManager.loadUsers();
            recommendationEngine = new RecommendationEngine(movies);
            System.out.println("Data loaded successfully!");
            System.out.println("Number of movies: " + movies.size());
            System.out.println("Number of users: " + users.size());
            System.out.println();
        } catch (IOException e) {
            System.err.println("Error: Unable to load data files: " + e.getMessage());
            System.exit(1);
        }

        // 主循环
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = showLoginMenu();
            } else {
                showMainMenu();
            }
        }

        // 保存数据并退出
        try {
            fileManager.saveUsers(users);
            System.out.println("Data saved. Goodbye!");
        } catch (IOException e) {
            System.err.println("Warning: Error saving data: " + e.getMessage());
        }
        scanner.close();
    }

    /**
     * 显示登录菜单
     * @return 如果选择退出返回false，否则返回true
     */
    private static boolean showLoginMenu() {
        System.out.println("=== Movie Recommendation & Tracker System ===");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.print("Please choose: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    return false;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, please enter a number.");
        }

        return true;
    }

    /**
     * 处理用户登录
     */
    private static void handleLogin() {
        System.out.print("Please enter username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Please enter password: ");
        String password = scanner.nextLine().trim();

        User user = users.get(username);
        if (user != null && user.verifyPassword(password)) {
            currentUser = user;
            System.out.println("Login successful! Welcome, " + username + "!");
            System.out.println();
        } else {
            System.out.println("Invalid username or password!");
        }
    }

    /**
     * 显示主菜单（登录后）
     */
    private static void showMainMenu() {
        System.out.println("=== Main Menu ===");
        System.out.println("Current user: " + currentUser.getUsername());
        System.out.println("1. Browse movies");
        System.out.println("2. Add movie to watchlist");
        System.out.println("3. Remove movie from watchlist");
        System.out.println("4. View watchlist");
        System.out.println("5. Mark movie as watched");
        System.out.println("6. View history");
        System.out.println("7. Get recommendations");
        System.out.println("8. Change password [Advanced Feature]");
        System.out.println("9. Create new account [Advanced Feature]");
        System.out.println("10. Change recommendation strategy [Advanced Feature]");
        System.out.println("11. Logout");
        System.out.print("Please choose: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            System.out.println();

            switch (choice) {
                case 1:
                    browseMovies();
                    break;
                case 2:
                    addToWatchlist();
                    break;
                case 3:
                    removeFromWatchlist();
                    break;
                case 4:
                    viewWatchlist();
                    break;
                case 5:
                    markAsWatched();
                    break;
                case 6:
                    viewHistory();
                    break;
                case 7:
                    getRecommendations();
                    break;
                case 8:
                    changePassword();
                    break;
                case 9:
                    createNewAccount();
                    break;
                case 10:
                    changeRecommendationStrategy();
                    break;
                case 11:
                    handleLogout();
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, please enter a number.");
        }

        System.out.println();
    }

    /**
     * 浏览所有电影
     */
    private static void browseMovies() {
        System.out.println("=== All Movies ===");
        if (movies.isEmpty()) {
            System.out.println("No movie data available.");
            return;
        }

        ArrayList<Movie> movieList = new ArrayList<>(movies.values());
        // Sort by ID
        movieList.sort((m1, m2) -> m1.getId().compareTo(m2.getId()));

        for (Movie movie : movieList) {
            System.out.println(movie);
        }
        System.out.println("Total: " + movies.size() + " movies");
    }

    /**
     * 添加电影到观看列表
     */
    private static void addToWatchlist() {
        System.out.print("Please enter movie ID: ");
        String movieId = scanner.nextLine().trim().toUpperCase();

        if (!movies.containsKey(movieId)) {
            System.out.println("Error: Movie ID does not exist.");
            return;
        }

        if (currentUser.getWatchlist().contains(movieId)) {
            System.out.println("This movie is already in your watchlist.");
            return;
        }

        if (currentUser.getHistory().contains(movieId)) {
            System.out.println("This movie is already in your history and cannot be added to watchlist.");
            return;
        }

        if (currentUser.getWatchlist().addMovie(movieId)) {
            System.out.println("Successfully added to watchlist!");
            Movie movie = movies.get(movieId);
            System.out.println("Movie: " + movie.getTitle());
        } else {
            System.out.println("Failed to add.");
        }
    }

    /**
     * 从观看列表移除电影
     */
    private static void removeFromWatchlist() {
        Watchlist watchlist = currentUser.getWatchlist();
        if (watchlist.isEmpty()) {
            System.out.println("Watchlist is empty.");
            return;
        }

        System.out.print("Please enter movie ID: ");
        String movieId = scanner.nextLine().trim().toUpperCase();

        if (watchlist.removeMovie(movieId)) {
            System.out.println("Successfully removed from watchlist!");
            Movie movie = movies.get(movieId);
            if (movie != null) {
                System.out.println("Movie: " + movie.getTitle());
            }
        } else {
            System.out.println("This movie is not in your watchlist.");
        }
    }

    /**
     * 查看观看列表
     */
    private static void viewWatchlist() {
        Watchlist watchlist = currentUser.getWatchlist();
        System.out.println("=== Watchlist ===");
        
        if (watchlist.isEmpty()) {
            System.out.println("Watchlist is empty.");
            return;
        }

        ArrayList<String> movieIds = watchlist.getMovieIds();
        for (int i = 0; i < movieIds.size(); i++) {
            String movieId = movieIds.get(i);
            Movie movie = movies.get(movieId);
            if (movie != null) {
                System.out.println((i + 1) + ". " + movie);
            } else {
                System.out.println((i + 1) + ". " + movieId + " (Movie data not found)");
            }
        }
        System.out.println("Total: " + watchlist.size() + " movies");
    }

    /**
     * 标记电影为已观看
     */
    private static void markAsWatched() {
        System.out.print("Please enter movie ID: ");
        String movieId = scanner.nextLine().trim().toUpperCase();

        if (!movies.containsKey(movieId)) {
            System.out.println("Error: Movie ID does not exist.");
            return;
        }

        if (currentUser.getHistory().contains(movieId)) {
            System.out.println("This movie is already in your history.");
            return;
        }

        // Get current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());

        if (currentUser.getHistory().addMovie(movieId, date)) {
            System.out.println("Successfully marked as watched!");
            Movie movie = movies.get(movieId);
            System.out.println("Movie: " + movie.getTitle());
            System.out.println("Watched date: " + date);

            // If the movie is in watchlist, remove it
            if (currentUser.getWatchlist().contains(movieId)) {
                currentUser.getWatchlist().removeMovie(movieId);
                System.out.println("Removed from watchlist.");
            }
        } else {
            System.out.println("Failed to mark as watched.");
        }
    }

    /**
     * 查看观看历史
     */
    private static void viewHistory() {
        History history = currentUser.getHistory();
        System.out.println("=== Viewing History ===");
        
        if (history.isEmpty()) {
            System.out.println("Viewing history is empty.");
            return;
        }

        ArrayList<String> movieIds = history.getMovieIds();
        for (int i = 0; i < movieIds.size(); i++) {
            String movieId = movieIds.get(i);
            Movie movie = movies.get(movieId);
            String date = history.getDate(movieId);
            if (movie != null) {
                System.out.println((i + 1) + ". " + movie + " | Watched date: " + date);
            } else {
                System.out.println((i + 1) + ". " + movieId + " (Movie data not found) | Watched date: " + date);
            }
        }
        System.out.println("Total: " + history.size() + " movies");
    }

    /**
     * 获取推荐
     */
    private static void getRecommendations() {
        System.out.print("Please enter number of recommendations (N): ");
        try {
            int n = Integer.parseInt(scanner.nextLine().trim());
            if (n <= 0) {
                System.out.println("Number of recommendations must be greater than 0.");
                return;
            }

            System.out.println("=== Recommended Movies (Strategy: " + recommendationEngine.getStrategy() + ") ===");
            ArrayList<Movie> recommendations = recommendationEngine.getRecommendations(currentUser, n);

            if (recommendations.isEmpty()) {
                System.out.println("No recommendations available.");
                return;
            }

            for (int i = 0; i < recommendations.size(); i++) {
                System.out.println((i + 1) + ". " + recommendations.get(i));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, please enter a number.");
        }
    }

    /**
     * 更改密码（高级功能）
     */
    private static void changePassword() {
        System.out.print("Please enter current password: ");
        String currentPassword = scanner.nextLine().trim();

        if (!currentUser.verifyPassword(currentPassword)) {
            System.out.println("Current password is incorrect!");
            return;
        }

        System.out.print("Please enter new password: ");
        String newPassword = scanner.nextLine().trim();

        if (newPassword.isEmpty()) {
            System.out.println("New password cannot be empty!");
            return;
        }

        currentUser.setPassword(newPassword);
        System.out.println("Password changed successfully!");
    }

    /**
     * 创建新账户（高级功能）
     */
    private static void createNewAccount() {
        System.out.print("Please enter new username: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty!");
            return;
        }

        if (users.containsKey(username)) {
            System.out.println("Username already exists!");
            return;
        }

        System.out.print("Please enter password: ");
        String password = scanner.nextLine().trim();

        if (password.isEmpty()) {
            System.out.println("Password cannot be empty!");
            return;
        }

        User newUser = new User(username, password);
        users.put(username, newUser);
        System.out.println("Account created successfully!");
        System.out.println("You can now login with the new account.");
    }

    /**
     * 更改推荐策略（高级功能）
     */
    private static void changeRecommendationStrategy() {
        System.out.println("Current recommendation strategy: " + recommendationEngine.getStrategy());
        System.out.println("Available strategies:");
        System.out.println("1. genre (Recommend by genre)");
        System.out.println("2. rating (Recommend by rating)");
        System.out.println("3. year (Recommend by year)");
        System.out.print("Please choose strategy (1-3): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1:
                    recommendationEngine.setStrategy("genre");
                    System.out.println("Recommendation strategy changed to: genre");
                    break;
                case 2:
                    recommendationEngine.setStrategy("rating");
                    System.out.println("Recommendation strategy changed to: rating");
                    break;
                case 3:
                    recommendationEngine.setStrategy("year");
                    System.out.println("Recommendation strategy changed to: year");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, please enter a number.");
        }
    }

    /**
     * 处理登出
     */
    private static void handleLogout() {
        try {
            fileManager.saveUsers(users);
            System.out.println("Data saved.");
        } catch (IOException e) {
            System.err.println("Warning: Error saving data: " + e.getMessage());
        }
        currentUser = null;
        System.out.println("Logged out.");
    }
}

