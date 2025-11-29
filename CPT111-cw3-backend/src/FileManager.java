import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileManager {
    private final String moviesFilePath;
    private final String usersFilePath;

    public FileManager(String moviesFilePath, String usersFilePath) {
        this.moviesFilePath = moviesFilePath;
        this.usersFilePath = usersFilePath;
    }

    public HashMap<String, Movie> loadMovies() throws IOException {
        HashMap<String, Movie> result = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(moviesFilePath))) {
            String line = reader.readLine(); // skip the top 
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = splitCsvLine(line);
                if (parts.length < 5) {
                    continue;
                }
                    String id = parts[0].trim();
                    String title = parts[1].trim();
                    String genre = parts[2].trim();
                int year = parseInt(parts[3], 0);
                double rating = parseDouble(parts[4], 0.0);
                Movie movie = MovieFactory.createMovie(id, title, genre, year, rating);
                result.put(id, movie);
            }
        }
        return result;
    }

    public HashMap<String, User> loadUsers() throws IOException {
        HashMap<String, User> result = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line = reader.readLine(); // header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = splitCsvLine(line);
                if (parts.length < 4) {
                    continue;
                }
                String username = parts[0].trim();
                String passwordValue = parts[1].trim();
                String role;
                if (parts.length >= 5) {
                    role = UserRole.fromString(parts[2]);
                } else {
                    role = UserRole.BASIC;
                }
                int watchlistIndex;
                if (parts.length >= 5) {
                    watchlistIndex = 3;
                } else {
                    watchlistIndex = 2;
                }
                int historyIndex;
                if (parts.length >= 5) {
                    historyIndex = 4;
                } else {
                    historyIndex = 3;
                }

                Watchlist watchlist = new Watchlist(getField(parts, watchlistIndex));
                History history = new History(getField(parts, historyIndex));

                User user = UserFactory.createUser(username, passwordValue, role, watchlist, history);
                result.put(username, user);
            }
        }
        return result;
    }

    public void saveUsers(HashMap<String, User> users) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath))) {
            writer.write("username,password,role,watchlist,history");
            writer.newLine();
            for (User user : users.values()) {
                List<String> row = new ArrayList<>();
                row.add(user.getUsername());
                row.add(user.getPasswordHash());
                row.add(user.getRole());
                row.add(user.getWatchlist().toString());
                row.add(user.getHistory().toString());
                writer.write(String.join(",", escapeCsv(row)));
                writer.newLine();
            }
        }
    }

    private static String[] splitCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        String current = "";
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(current);
                current = "";
            } else {
                current = current + c;
            }
        }
        tokens.add(current);
        return tokens.toArray(new String[0]);
    }

    private static List<String> escapeCsv(List<String> values) {
        List<String> escaped = new ArrayList<>();
        for (String value : values) {
            if (value == null) {
                escaped.add("");
                continue;
            }
            if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                escaped.add("\"" + value.replace("\"", "\"\"") + "\"");
            } else {
                escaped.add(value);
            }
        }
        return escaped;
    }

    private static String getField(String[] parts, int index) {
        if (index >= parts.length) {
            return "";
        }
        return parts[index];
    }

    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
