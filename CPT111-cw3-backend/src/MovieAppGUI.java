import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.util.Callback;

public class MovieAppGUI extends Application {
    private static String sharedMoviesPath;
    private static String sharedUsersPath;

    private FileManager fileManager;
    private HashMap<String, Movie> movies;
    private HashMap<String, User> users;
    private RecommendationEngine recommendationEngine;
    private User currentUser;

    private Stage primaryStage;
    private ListView<Movie> movieListView;
    private ListView<String> watchlistView;
    private ListView<String> historyView;
    private TextArea recommendationArea;
    private Label headerLabel;
    private Label statusLabel;

    public static void launchApp(String moviesPath, String usersPath) {
        sharedMoviesPath = moviesPath;
        sharedUsersPath = usersPath;
        launch();
    }

    public static void main(String[] args) {
        sharedMoviesPath = "CW3_Data_Files/data/movies.csv";
        sharedUsersPath = "CW3_Data_Files/data/users.csv";
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        primaryStage.setTitle("Movie Recommendation & Tracker (JavaFX)");
        try {
            this.fileManager = new FileManager(sharedMoviesPath, sharedUsersPath);
            this.movies = fileManager.loadMovies();
            this.users = fileManager.loadUsers();
            this.recommendationEngine = new RecommendationEngine(movies);
        } catch (IOException e) {
            showFatalError("无法加载数据文件: " + e.getMessage());
            return;
        }
        showLoginScene();
        primaryStage.show();
    }

    private void showLoginScene() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(24));

        Label title = new Label("Movie Recommendation & Tracker");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label messageLabel = new Label();

        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                attemptGuiLogin(usernameField.getText(), passwordField.getText(), messageLabel);
            }
        });

        Button registerButton = new Button("Register");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleGuiRegistration(messageLabel);
            }
        });

        Button exitButton = new Button("Exit");
        exitButton.setMaxWidth(Double.MAX_VALUE);
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
            }
        });

        root.getChildren().addAll(title, usernameField, passwordField, loginButton, registerButton, exitButton, messageLabel);
        Scene scene = new Scene(root, 420, 320);
        primaryStage.setScene(scene);
    }

    private void attemptGuiLogin(String username, String password, Label messageLabel) {
        if (username == null || username.isEmpty()) {
            messageLabel.setText("Username cannot be empty.");
            return;
        }
        User user = users.get(username);
        if (user != null && user.verifyPassword(password)) {
            currentUser = user;
            showDashboardScene();
        } else {
            messageLabel.setText("Invalid username or password.");
        }
    }

    private void handleGuiRegistration(Label messageLabel) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Register new account");
        dialog.setHeaderText("Create a new account");
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm Password");
        ChoiceBox<String> roleChoice = new ChoiceBox<>(FXCollections.observableArrayList("Basic", "Premium"));
        roleChoice.getSelectionModel().selectFirst();

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Confirm:"), 0, 2);
        grid.add(confirmField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleChoice, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(new Callback<ButtonType, User>() {
            @Override
            public User call(ButtonType dialogButton) {
                if (dialogButton == createButtonType) {
                    String username = usernameField.getText().trim();
                    String password = passwordField.getText();
                    String confirm = confirmField.getText();
                    if (username.isEmpty() || password.isEmpty()) {
                        messageLabel.setText("Username and password cannot be empty.");
                        return null;
                    }
                    if (!password.equals(confirm)) {
                        messageLabel.setText("Passwords do not match.");
                        return null;
                    }
                    if (users.containsKey(username)) {
                        messageLabel.setText("Username already exists.");
                        return null;
                    }
                    String roleValue;
                    if (roleChoice.getValue().equalsIgnoreCase("premium")) {
                        roleValue = UserRole.PREMIUM;
                    } else {
                        roleValue = UserRole.BASIC;
                    }
                    return UserFactory.createUser(username, password, roleValue);
                }
                return null;
            }
        });

        Optional<User> result = dialog.showAndWait();
        if (result.isPresent()) {
            User newUser = result.get();
            users.put(newUser.getUsername(), newUser);
            persistUsersSafely();
            messageLabel.setText("Account created! You can now login.");
        }
    }

    private void showDashboardScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setTop(buildHeader());
        root.setCenter(buildCenterPane());
        root.setRight(buildSidePane());

        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setScene(scene);
        refreshAllLists();
    }

    private VBox buildHeader() {
        VBox container = new VBox(8);
        container.setPadding(new Insets(0, 0, 12, 0));

        headerLabel = new Label();
        headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        statusLabel = new Label();

        HBox buttons = new HBox(10);
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentUser = null;
                recommendationArea = null;
                showLoginScene();
            }
        });

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showPasswordDialog();
            }
        });

        Button refreshRecButton = new Button("Get Recommendations");
        refreshRecButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateRecommendations();
            }
        });

        buttons.getChildren().addAll(logoutButton, changePasswordButton, refreshRecButton);
        container.getChildren().addAll(headerLabel, statusLabel, buttons);
        return container;
    }

    private VBox buildCenterPane() {
        VBox center = new VBox(10);
        Label label = new Label("All Movies");
        movieListView = new ListView<>();
        movieListView.setCellFactory(new Callback<ListView<Movie>, ListCell<Movie>>() {
            @Override
            public ListCell<Movie> call(ListView<Movie> list) {
                return new ListCell<Movie>() {
                    @Override
                    protected void updateItem(Movie item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("");
                        } else {
                            setText(item.toString());
                        }
                    }
                };
            }
        });
        Button addButton = new Button("Add selected movie to watchlist");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addSelectedMovieToWatchlist();
            }
        });

        VBox.setVgrow(movieListView, Priority.ALWAYS);
        center.getChildren().addAll(label, movieListView, addButton);
        return center;
    }

    private VBox buildSidePane() {
        VBox side = new VBox(12);
        side.setPrefWidth(420);

        // Watchlist section
        Label watchlistLabel = new Label("Watchlist");
        watchlistView = new ListView<>();
        Button removeButton = new Button("Remove");
        removeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                removeSelectedFromWatchlist();
            }
        });
        Button markWatchedButton = new Button("Mark as watched");
        markWatchedButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                markSelectedAsWatched();
            }
        });
        HBox watchlistButtons = new HBox(10, removeButton, markWatchedButton);

        // History section
        Label historyLabel = new Label("History");
        historyView = new ListView<>();

        // Recommendation section
        Label recommendationLabel = new Label("Recommendations");
        recommendationArea = new TextArea();
        recommendationArea.setEditable(false);
        recommendationArea.setWrapText(true);
        recommendationArea.setPrefRowCount(8);

        VBox.setVgrow(watchlistView, Priority.SOMETIMES);
        VBox.setVgrow(historyView, Priority.SOMETIMES);
        VBox.setVgrow(recommendationArea, Priority.ALWAYS);

        side.getChildren().addAll(
                watchlistLabel,
                watchlistView,
                watchlistButtons,
                historyLabel,
                historyView,
                recommendationLabel,
                recommendationArea
        );
        return side;
    }

    private void refreshAllLists() {
        if (currentUser == null) {
            return;
        }
        ArrayList<Movie> allMovies = new ArrayList<>(movies.values());
        movieListView.setItems(FXCollections.observableArrayList(allMovies));

        ObservableList<String> watchItems = FXCollections.observableArrayList();
        ArrayList<String> watchIds = currentUser.getWatchlist().getMovieIds();
        for (String id : watchIds) {
            watchItems.add(formatMovieLine(id));
        }
        watchlistView.setItems(watchItems);

        ObservableList<String> historyItems = FXCollections.observableArrayList();
        ArrayList<String> historyIds = currentUser.getHistory().getMovieIds();
        for (String id : historyIds) {
            String date = currentUser.getHistory().getDate(id);
            String dateDisplay;
            if (date == null) {
                dateDisplay = "-";
            } else {
                dateDisplay = date;
            }
            historyItems.add(formatMovieLine(id) + " | Watched: " + dateDisplay);
        }
        historyView.setItems(historyItems);

        updateHeaderText();
        updateRecommendations();
    }

    private void updateHeaderText() {
        if (currentUser == null) {
            return;
        }
        int watchlistLimit = currentUser.getWatchlistLimit();
        String limitText;
        if (watchlistLimit == Integer.MAX_VALUE) {
            limitText = "∞";
        } else {
            limitText = String.valueOf(watchlistLimit);
        }
        headerLabel.setText(String.format("User: %s (%s) | Watchlist %d/%s | Strategy: %s",
                currentUser.getUsername(),
                currentUser.getRole(),
                currentUser.getWatchlist().size(),
                limitText,
                recommendationEngine.getStrategyName()));
    }

    private void updateRecommendations() {
        if (currentUser == null || recommendationArea == null) {
            return;
        }
        ArrayList<Movie> recommendations = recommendationEngine.getRecommendations(
                currentUser,
                currentUser.getRecommendationLimit());
        if (recommendations.isEmpty()) {
            recommendationArea.setText("No recommendations available. Try watching or adding more movies!");
        } else {
            String text = "";
            for (int i = 0; i < recommendations.size(); i++) {
                text = text + (i + 1) + ". " + recommendations.get(i).toString() + "\n";
            }
            recommendationArea.setText(text);
        }
    }

    private String formatMovieLine(String movieId) {
        Movie movie = movies.get(movieId);
        if (movie == null) {
            return movieId + " (not found)";
        }
        return movie.toString();
    }

    private void addSelectedMovieToWatchlist() {
        Movie selected = movieListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select a movie first.");
            return;
        }
        if (!movies.containsKey(selected.getId())) {
            statusLabel.setText("Movie does not exist.");
            return;
        }
        if (currentUser.getWatchlist().contains(selected.getId())) {
            statusLabel.setText("Movie already in watchlist.");
            return;
        }
        if (currentUser.getHistory().contains(selected.getId())) {
            statusLabel.setText("Movie already watched.");
            return;
        }
        if (!currentUser.canAddToWatchlist()) {
            statusLabel.setText("Watchlist limit reached for your account.");
            return;
        }
        if (currentUser.getWatchlist().addMovie(selected.getId())) {
            persistUsersSafely();
            statusLabel.setText("Added to watchlist: " + selected.getTitle());
            refreshAllLists();
        }
    }

    private void removeSelectedFromWatchlist() {
        int index = watchlistView.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            statusLabel.setText("Select an entry in watchlist first.");
            return;
        }
        String movieId = currentUser.getWatchlist().getMovieIds().get(index);
        if (currentUser.getWatchlist().removeMovie(movieId)) {
            persistUsersSafely();
            statusLabel.setText("Removed from watchlist.");
            refreshAllLists();
        }
    }

    private void markSelectedAsWatched() {
        int index = watchlistView.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            statusLabel.setText("Select an entry in watchlist to mark as watched.");
            return;
        }
        String movieId = currentUser.getWatchlist().getMovieIds().get(index);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = today.format(formatter);
        if (currentUser.getHistory().addMovie(movieId, date)) {
            currentUser.getWatchlist().removeMovie(movieId);
            persistUsersSafely();
            statusLabel.setText("Marked as watched.");
            refreshAllLists();
        }
    }

    private void showPasswordDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Update your password");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        PasswordField currentField = new PasswordField();
        currentField.setPromptText("Current password");
        PasswordField newField = new PasswordField();
        newField.setPromptText("New password");

        VBox box = new VBox(10, new Label("Current password:"), currentField, new Label("New password:"), newField);
        dialog.getDialogPane().setContent(box);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (!currentUser.verifyPassword(currentField.getText())) {
                statusLabel.setText("Current password incorrect.");
                return;
            }
            if (newField.getText().isEmpty()) {
                statusLabel.setText("New password cannot be empty.");
                return;
            }
            currentUser.setPassword(newField.getText());
            persistUsersSafely();
            statusLabel.setText("Password updated successfully.");
        }
    }

    private void persistUsersSafely() {
        try {
            fileManager.saveUsers(users);
        } catch (IOException e) {
            statusLabel.setText("Error saving users: " + e.getMessage());
        }
    }

    private void showFatalError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fatal Error");
        alert.setHeaderText("Application cannot start");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

