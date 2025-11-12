import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * 使用 JavaFX 为电影推荐与跟踪系统提供图形界面
 */
public class MovieApp extends Application {

    private static final String MOVIES_PATH = "CW3_Data_Files/data/movies.csv";
    private static final String USERS_PATH = "CW3_Data_Files/data/users.csv";

    private HashMap<String, Movie> movies;
    private HashMap<String, User> users;
    private User currentUser;
    private FileManager fileManager;
    private RecommendationEngine recommendationEngine;

    private Stage primaryStage;
    private Scene loginScene;
    private Scene mainScene;

    private TextField loginUsernameField;
    private PasswordField loginPasswordField;
    private Label loginMessageLabel;

    private TableView<Movie> movieTable;
    private TableView<Movie> watchlistTable;
    private TableView<HistoryItem> historyTable;
    private TableView<Movie> recommendationTable;

    private ObservableList<Movie> allMoviesData;
    private ObservableList<Movie> watchlistData;
    private ObservableList<HistoryItem> historyData;
    private ObservableList<Movie> recommendationData;

    private TabPane mainTabPane;
    private ComboBox<StrategyOption> strategyComboBox;
    private Spinner<Integer> recommendSpinner;
    private Button logoutButton;
    private Label welcomeLabel;
    private Label statusLabel;
    private Label settingsUserLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("电影推荐与跟踪系统");

        if (!initializeData()) {
            return;
        }

        initializeScenes();

        primaryStage.setScene(loginScene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.setOnCloseRequest(event -> {
            saveUsersData();
            Platform.exit();
        });
        primaryStage.show();
    }

    @Override
    public void stop() {
        saveUsersData();
    }

    private boolean initializeData() {
        try {
            fileManager = new FileManager(MOVIES_PATH, USERS_PATH);
            movies = fileManager.loadMovies();
            users = fileManager.loadUsers();
            recommendationEngine = new RecommendationEngine(movies);
            return true;
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "数据加载失败", "无法加载 CSV 数据文件。\n" + e.getMessage());
            Platform.exit();
            return false;
        }
    }

    private void initializeScenes() {
        allMoviesData = FXCollections.observableArrayList();
        watchlistData = FXCollections.observableArrayList();
        historyData = FXCollections.observableArrayList();
        recommendationData = FXCollections.observableArrayList();

        loginScene = createLoginScene();
        mainScene = createMainScene();

        refreshAllMoviesData();
    }

    private Scene createLoginScene() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(40));
        grid.setHgap(15);
        grid.setVgap(18);

        Label titleLabel = new Label("电影推荐与跟踪系统");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label usernameLabel = new Label("用户名：");
        loginUsernameField = new TextField();
        loginUsernameField.setPromptText("请输入用户名");

        Label passwordLabel = new Label("密码：");
        loginPasswordField = new PasswordField();
        loginPasswordField.setPromptText("请输入密码");

        loginMessageLabel = new Label();
        loginMessageLabel.setStyle("-fx-text-fill: #d9534f;");

        Button loginButton = new Button("登录");
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(event -> handleLogin());

        Button registerButton = new Button("注册新账户");
        registerButton.setOnAction(event -> showRegistrationDialog());

        Button exitButton = new Button("退出");
        exitButton.setOnAction(event -> {
            saveUsersData();
            Platform.exit();
        });

        HBox buttonBox = new HBox(15, loginButton, registerButton, exitButton);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(usernameLabel, 0, 1);
        grid.add(loginUsernameField, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(loginPasswordField, 1, 2);
        grid.add(buttonBox, 0, 3, 2, 1);
        grid.add(loginMessageLabel, 0, 4, 2, 1);

        return new Scene(grid);
    }

    private Scene createMainScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        welcomeLabel = new Label("欢迎，请先登录。");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label strategyLabel = new Label("推荐策略：");
        strategyComboBox = new ComboBox<>();
        strategyComboBox.setItems(FXCollections.observableArrayList(
                new StrategyOption("genre", "按类型"),
                new StrategyOption("rating", "按评分"),
                new StrategyOption("year", "按年份")
        ));
        strategyComboBox.setDisable(true);
        strategyComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && recommendationEngine != null) {
                recommendationEngine.setStrategy(newValue.code());
                setStatus("推荐策略已切换为：" + newValue.display());
            }
        });

        logoutButton = new Button("登出");
        logoutButton.setDisable(true);
        logoutButton.setOnAction(event -> handleLogout());

        header.getChildren().addAll(welcomeLabel, spacer, strategyLabel, strategyComboBox, logoutButton);
        root.setTop(header);

        mainTabPane = new TabPane();
        mainTabPane.getTabs().addAll(
                createMoviesTab(),
                createWatchlistTab(),
                createHistoryTab(),
                createRecommendationsTab(),
                createSettingsTab()
        );
        mainTabPane.setDisable(true);
        root.setCenter(mainTabPane);

        statusLabel = new Label("请先登录。");
        statusLabel.setPadding(new Insets(12, 0, 0, 5));
        root.setBottom(statusLabel);

        return new Scene(root);
    }

    private Tab createMoviesTab() {
        Tab tab = new Tab("所有电影");
        tab.setClosable(false);

        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));

        movieTable = new TableView<>();
        movieTable.setItems(allMoviesData);
        movieTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        movieTable.setPlaceholder(new Label("暂无电影数据"));

        TableColumn<Movie, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Movie, String> titleCol = new TableColumn<>("标题");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Movie, String> genreCol = new TableColumn<>("类型");
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));

        TableColumn<Movie, Integer> yearCol = new TableColumn<>("年份");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<Movie, Double> ratingCol = new TableColumn<>("评分");
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));

        movieTable.getColumns().addAll(idCol, titleCol, genreCol, yearCol, ratingCol);

        Button addButton = new Button("添加到观看列表");
        addButton.setOnAction(event -> {
            if (!ensureLoggedIn()) {
                return;
            }
            Movie selected = movieTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(AlertType.WARNING, "操作提示", "请先选择一部电影。");
                return;
            }
            handleAddToWatchlist(selected);
        });

        Button markButton = new Button("标记为已观看");
        markButton.setOnAction(event -> {
            if (!ensureLoggedIn()) {
                return;
            }
            Movie selected = movieTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(AlertType.WARNING, "操作提示", "请先选择一部电影。");
                return;
            }
            handleMarkAsWatched(selected);
        });

        HBox buttons = new HBox(15, addButton, markButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        pane.setCenter(movieTable);
        pane.setBottom(buttons);

        tab.setContent(pane);
        return tab;
    }

    private Tab createWatchlistTab() {
        Tab tab = new Tab("观看列表");
        tab.setClosable(false);

        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));

        watchlistTable = new TableView<>();
        watchlistTable.setItems(watchlistData);
        watchlistTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        watchlistTable.setPlaceholder(new Label("观看列表为空"));

        TableColumn<Movie, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Movie, String> titleCol = new TableColumn<>("标题");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Movie, String> genreCol = new TableColumn<>("类型");
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));

        TableColumn<Movie, Integer> yearCol = new TableColumn<>("年份");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<Movie, Double> ratingCol = new TableColumn<>("评分");
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));

        watchlistTable.getColumns().addAll(idCol, titleCol, genreCol, yearCol, ratingCol);

        Button removeButton = new Button("从观看列表移除");
        removeButton.setOnAction(event -> {
            if (!ensureLoggedIn()) {
                return;
            }
            Movie selected = watchlistTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(AlertType.WARNING, "操作提示", "请先选择一部电影。");
                return;
            }
            handleRemoveFromWatchlist(selected);
        });

        Button markButton = new Button("标记为已观看");
        markButton.setOnAction(event -> {
            if (!ensureLoggedIn()) {
                return;
            }
            Movie selected = watchlistTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(AlertType.WARNING, "操作提示", "请先选择一部电影。");
                return;
            }
            handleMarkAsWatched(selected);
        });

        HBox buttons = new HBox(15, removeButton, markButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        pane.setCenter(watchlistTable);
        pane.setBottom(buttons);

        tab.setContent(pane);
        return tab;
    }

    private Tab createHistoryTab() {
        Tab tab = new Tab("观看历史");
        tab.setClosable(false);

        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));

        historyTable = new TableView<>();
        historyTable.setItems(historyData);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.setPlaceholder(new Label("观看历史为空"));

        TableColumn<HistoryItem, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> cell.getValue().movieIdProperty());

        TableColumn<HistoryItem, String> titleCol = new TableColumn<>("标题");
        titleCol.setCellValueFactory(cell -> cell.getValue().titleProperty());

        TableColumn<HistoryItem, String> genreCol = new TableColumn<>("类型");
        genreCol.setCellValueFactory(cell -> cell.getValue().genreProperty());

        TableColumn<HistoryItem, String> yearCol = new TableColumn<>("年份");
        yearCol.setCellValueFactory(cell -> cell.getValue().yearProperty());

        TableColumn<HistoryItem, String> ratingCol = new TableColumn<>("评分");
        ratingCol.setCellValueFactory(cell -> cell.getValue().ratingProperty());

        TableColumn<HistoryItem, String> dateCol = new TableColumn<>("观看日期");
        dateCol.setCellValueFactory(cell -> cell.getValue().dateProperty());

        historyTable.getColumns().addAll(idCol, titleCol, genreCol, yearCol, ratingCol, dateCol);

        pane.setCenter(historyTable);

        tab.setContent(pane);
        return tab;
    }

    private Tab createRecommendationsTab() {
        Tab tab = new Tab("电影推荐");
        tab.setClosable(false);

        VBox container = new VBox(15);
        container.setPadding(new Insets(20));

        Label description = new Label("选择推荐数量并点击生成，系统将根据当前策略提供推荐。");

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_LEFT);

        Label countLabel = new Label("推荐数量：");
        recommendSpinner = new Spinner<>();
        recommendSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 5));
        recommendSpinner.setDisable(true);

        Button generateButton = new Button("生成推荐");
        generateButton.setOnAction(event -> generateRecommendations());

        controls.getChildren().addAll(countLabel, recommendSpinner, generateButton);

        recommendationTable = new TableView<>();
        recommendationTable.setItems(recommendationData);
        recommendationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        recommendationTable.setPlaceholder(new Label("尚未生成推荐"));

        TableColumn<Movie, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Movie, String> titleCol = new TableColumn<>("标题");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Movie, String> genreCol = new TableColumn<>("类型");
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));

        TableColumn<Movie, Integer> yearCol = new TableColumn<>("年份");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<Movie, Double> ratingCol = new TableColumn<>("评分");
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));

        recommendationTable.getColumns().addAll(idCol, titleCol, genreCol, yearCol, ratingCol);

        container.getChildren().addAll(description, controls, recommendationTable);
        tab.setContent(container);
        return tab;
    }

    private Tab createSettingsTab() {
        Tab tab = new Tab("账户设置");
        tab.setClosable(false);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(12);
        grid.setVgap(15);

        Label sectionTitle = new Label("修改密码");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label userLabel = new Label("当前用户：");
        settingsUserLabel = new Label("未登录");

        Label currentPasswordLabel = new Label("当前密码：");
        PasswordField currentPasswordField = new PasswordField();

        Label newPasswordLabel = new Label("新密码：");
        PasswordField newPasswordField = new PasswordField();

        Label confirmPasswordLabel = new Label("确认新密码：");
        PasswordField confirmPasswordField = new PasswordField();

        Label feedbackLabel = new Label();

        Button changeButton = new Button("修改密码");
        changeButton.setOnAction(event -> handleChangePassword(
                currentPasswordField,
                newPasswordField,
                confirmPasswordField,
                feedbackLabel
        ));

        grid.add(sectionTitle, 0, 0, 2, 1);
        grid.add(userLabel, 0, 1);
        grid.add(settingsUserLabel, 1, 1);
        grid.add(currentPasswordLabel, 0, 2);
        grid.add(currentPasswordField, 1, 2);
        grid.add(newPasswordLabel, 0, 3);
        grid.add(newPasswordField, 1, 3);
        grid.add(confirmPasswordLabel, 0, 4);
        grid.add(confirmPasswordField, 1, 4);
        grid.add(changeButton, 0, 5, 2, 1);
        grid.add(feedbackLabel, 0, 6, 2, 1);

        tab.setContent(grid);
        return tab;
    }

    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginMessageLabel.setText("请输入用户名和密码。");
            loginMessageLabel.setStyle("-fx-text-fill: #d9534f;");
            return;
        }

        User user = users.get(username);
        if (user != null && user.verifyPassword(password)) {
            currentUser = user;
            loginMessageLabel.setText("");
            welcomeLabel.setText("欢迎，" + username + "！");
            settingsUserLabel.setText(username);
            loginUsernameField.clear();
            loginPasswordField.clear();
            updateUiForLoggedInState();
            setStatus("登录成功，欢迎 " + username + "！");
            primaryStage.setScene(mainScene);
            primaryStage.centerOnScreen();
        } else {
            loginMessageLabel.setText("用户名或密码错误，请重试。");
            loginMessageLabel.setStyle("-fx-text-fill: #d9534f;");
        }
    }

    private void handleLogout() {
        if (currentUser != null) {
            setStatus("用户 " + currentUser.getUsername() + " 已登出。");
        }
        currentUser = null;
        settingsUserLabel.setText("未登录");
        welcomeLabel.setText("欢迎，请先登录。");
        watchlistData.clear();
        historyData.clear();
        recommendationData.clear();
        mainTabPane.setDisable(true);
        strategyComboBox.setDisable(true);
        recommendSpinner.setDisable(true);
        logoutButton.setDisable(true);
        primaryStage.setScene(loginScene);
    }

    private void updateUiForLoggedInState() {
        refreshWatchlistData();
        refreshHistoryData();
        recommendationData.clear();
        mainTabPane.setDisable(false);
        strategyComboBox.setDisable(false);
        recommendSpinner.setDisable(false);
        logoutButton.setDisable(false);
        selectStrategyOption(recommendationEngine.getStrategy());
        mainTabPane.getSelectionModel().selectFirst();
    }

    private void selectStrategyOption(String code) {
        for (StrategyOption option : strategyComboBox.getItems()) {
            if (option.code().equals(code)) {
                strategyComboBox.getSelectionModel().select(option);
                break;
            }
        }
    }

    private void handleAddToWatchlist(Movie movie) {
        Movie actual = movies.get(movie.getId());
        if (actual == null) {
            showAlert(AlertType.ERROR, "数据错误", "无法找到该电影的详细信息。");
            return;
        }
        if (currentUser.getWatchlist().contains(actual.getId())) {
            showAlert(AlertType.INFORMATION, "提示", "该电影已经在观看列表中。");
            return;
        }
        if (currentUser.getHistory().contains(actual.getId())) {
            showAlert(AlertType.WARNING, "提示", "该电影已经被标记为已观看。");
            return;
        }
        if (currentUser.getWatchlist().addMovie(actual.getId())) {
            refreshWatchlistData();
            saveUsersData();
            setStatus("已将《" + actual.getTitle() + "》添加到观看列表。");
        }
    }

    private void handleRemoveFromWatchlist(Movie movie) {
        if (currentUser.getWatchlist().removeMovie(movie.getId())) {
            refreshWatchlistData();
            saveUsersData();
            setStatus("已从观看列表移除《" + movie.getTitle() + "》。");
        } else {
            showAlert(AlertType.INFORMATION, "提示", "该电影不在观看列表中。");
        }
    }

    private void handleMarkAsWatched(Movie movie) {
        Movie actual = movies.get(movie.getId());
        if (actual == null) {
            showAlert(AlertType.ERROR, "数据错误", "无法找到该电影的详细信息。");
            return;
        }
        if (currentUser.getHistory().contains(actual.getId())) {
            showAlert(AlertType.INFORMATION, "提示", "该电影已经在观看历史中。");
            return;
        }

        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (currentUser.getHistory().addMovie(actual.getId(), date)) {
            if (currentUser.getWatchlist().contains(actual.getId())) {
                currentUser.getWatchlist().removeMovie(actual.getId());
            }
            refreshWatchlistData();
            refreshHistoryData();
            saveUsersData();
            setStatus("已标记观看《" + actual.getTitle() + "》，日期：" + date);
        } else {
            showAlert(AlertType.ERROR, "操作失败", "无法标记该电影为已观看。");
        }
    }

    private void generateRecommendations() {
        if (!ensureLoggedIn()) {
            return;
        }
        int count = recommendSpinner.getValue();
        List<Movie> results = recommendationEngine.getRecommendations(currentUser, count);
        recommendationData.setAll(results);
        if (results.isEmpty()) {
            setStatus("暂无可用推荐，请先丰富观看历史。");
        } else {
            setStatus("已生成 " + results.size() + " 条推荐。");
        }
    }

    private void handleChangePassword(PasswordField currentField,
                                      PasswordField newField,
                                      PasswordField confirmField,
                                      Label feedbackLabel) {
        if (!ensureLoggedIn()) {
            feedbackLabel.setText("请先登录。");
            feedbackLabel.setStyle("-fx-text-fill: #d9534f;");
            return;
        }

        String currentPassword = currentField.getText();
        String newPassword = newField.getText();
        String confirmPassword = confirmField.getText();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            feedbackLabel.setText("请填写所有字段。");
            feedbackLabel.setStyle("-fx-text-fill: #d9534f;");
            return;
        }
        if (!currentUser.verifyPassword(currentPassword)) {
            feedbackLabel.setText("当前密码不正确。");
            feedbackLabel.setStyle("-fx-text-fill: #d9534f;");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            feedbackLabel.setText("两次输入的新密码不一致。");
            feedbackLabel.setStyle("-fx-text-fill: #d9534f;");
            return;
        }

        currentUser.setPassword(newPassword);
        saveUsersData();
        feedbackLabel.setText("密码修改成功。");
        feedbackLabel.setStyle("-fx-text-fill: #28a745;");
        currentField.clear();
        newField.clear();
        confirmField.clear();
    }

    private void showRegistrationDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("注册新账户");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        usernameField.setPromptText("用户名");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("密码");

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("再次输入密码");

        Label feedback = new Label();
        feedback.setStyle("-fx-text-fill: #d9534f;");

        grid.add(new Label("用户名："), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("密码："), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("确认密码："), 0, 2);
        grid.add(confirmField, 1, 2);
        grid.add(feedback, 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String confirm = confirmField.getText();

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                feedback.setText("请填写所有字段。");
                event.consume();
                return;
            }
            if (!password.equals(confirm)) {
                feedback.setText("两次输入的密码不一致。");
                event.consume();
                return;
            }
            if (users.containsKey(username)) {
                feedback.setText("用户名已存在，请换一个。");
                event.consume();
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            User newUser = new User(username, password);
            users.put(username, newUser);
            saveUsersData();
            loginUsernameField.setText(username);
            loginPasswordField.setText(password);
            loginMessageLabel.setText("账户创建成功，请登录。");
            loginMessageLabel.setStyle("-fx-text-fill: #28a745;");
            setStatus("已创建新账户：" + username);
        }
    }

    private boolean ensureLoggedIn() {
        if (currentUser == null) {
            showAlert(AlertType.WARNING, "未登录", "请先登录账户再执行此操作。");
            return false;
        }
        return true;
    }

    private void refreshAllMoviesData() {
        if (movies == null) {
            return;
        }
        ArrayList<Movie> list = new ArrayList<>(movies.values());
        list.sort(Comparator.comparing(Movie::getId));
        allMoviesData.setAll(list);
    }

    private void refreshWatchlistData() {
        if (currentUser == null) {
            watchlistData.clear();
            return;
        }
        ArrayList<String> ids = currentUser.getWatchlist().getMovieIds();
        List<Movie> data = new ArrayList<>();
        for (String id : ids) {
            Movie movie = movies.get(id);
            if (movie != null) {
                data.add(movie);
            } else {
                data.add(new Movie(id, "未知电影", "未知", 0, 0.0));
            }
        }
        data.sort(Comparator.comparing(Movie::getId));
        watchlistData.setAll(data);
    }

    private void refreshHistoryData() {
        if (currentUser == null) {
            historyData.clear();
            return;
        }
        ArrayList<String> ids = currentUser.getHistory().getMovieIds();
        ArrayList<HistoryItem> items = new ArrayList<>();
        for (String id : ids) {
            Movie movie = movies.get(id);
            String title = movie != null ? movie.getTitle() : "未知电影";
            String genre = movie != null ? movie.getGenre() : "未知";
            String year = movie != null ? String.valueOf(movie.getYear()) : "-";
            String rating = movie != null ? String.format("%.1f", movie.getRating()) : "-";
            String date = currentUser.getHistory().getDate(id);
            items.add(new HistoryItem(id, title, genre, year, rating, date));
        }
        historyData.setAll(items);
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    private void saveUsersData() {
        if (fileManager == null || users == null) {
            return;
        }
        try {
            fileManager.saveUsers(users);
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "保存失败", "保存用户数据时发生错误。\n" + e.getMessage());
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class HistoryItem {
        private final SimpleStringProperty movieId;
        private final SimpleStringProperty title;
        private final SimpleStringProperty genre;
        private final SimpleStringProperty year;
        private final SimpleStringProperty rating;
        private final SimpleStringProperty date;

        HistoryItem(String movieId, String title, String genre, String year, String rating, String date) {
            this.movieId = new SimpleStringProperty(movieId);
            this.title = new SimpleStringProperty(title);
            this.genre = new SimpleStringProperty(genre);
            this.year = new SimpleStringProperty(year);
            this.rating = new SimpleStringProperty(rating);
            this.date = new SimpleStringProperty(date);
        }

        SimpleStringProperty movieIdProperty() {
            return movieId;
        }

        SimpleStringProperty titleProperty() {
            return title;
        }

        SimpleStringProperty genreProperty() {
            return genre;
        }

        SimpleStringProperty yearProperty() {
            return year;
        }

        SimpleStringProperty ratingProperty() {
            return rating;
        }

        SimpleStringProperty dateProperty() {
            return date;
        }
    }

    private static class StrategyOption {
        private final String code;
        private final String display;

        StrategyOption(String code, String display) {
            this.code = code;
            this.display = display;
        }

        String code() {
            return code;
        }

        String display() {
            return display;
        }

        @Override
        public String toString() {
            return display;
        }
    }
}

