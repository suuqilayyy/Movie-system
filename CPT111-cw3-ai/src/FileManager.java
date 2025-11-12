import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * FileManager类处理CSV文件的读写操作
 * 负责加载电影数据和用户数据，以及保存用户数据
 */
public class FileManager {
    private String moviesFilePath;
    private String usersFilePath;

    /**
     * 构造函数
     * @param moviesFilePath 电影数据文件路径
     * @param usersFilePath 用户数据文件路径
     */
    public FileManager(String moviesFilePath, String usersFilePath) {
        this.moviesFilePath = moviesFilePath;
        this.usersFilePath = usersFilePath;
    }

    /**
     * 从CSV文件加载电影数据
     * @return HashMap，键为电影ID，值为Movie对象
     * @throws IOException 如果文件读取失败
     */
    public HashMap<String, Movie> loadMovies() throws IOException {
        HashMap<String, Movie> movies = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(moviesFilePath))) {
            String line = reader.readLine(); // Skip header line
            if (line == null) {
                throw new IOException("Movie file is empty or format error");
            }

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    String[] parts = parseCSVLine(line);
                    if (parts.length >= 5) {
                        String id = parts[0].trim();
                        String title = parts[1].trim();
                        String genre = parts[2].trim();
                        int year = Integer.parseInt(parts[3].trim());
                        double rating = Double.parseDouble(parts[4].trim());

                        Movie movie = new Movie(id, title, genre, year, rating);
                        movies.put(id, movie);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Warning: Skipping invalid movie data line: " + line);
                }
            }
        }

        return movies;
    }

    /**
     * 从CSV文件加载用户数据
     * @return HashMap，键为用户名，值为User对象
     * @throws IOException 如果文件读取失败
     */
    public HashMap<String, User> loadUsers() throws IOException {
        HashMap<String, User> users = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line = reader.readLine(); // Skip header line
            if (line == null) {
                throw new IOException("User file is empty or format error");
            }

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    String[] parts = parseCSVLine(line);
                    if (parts.length >= 4) {
                        String username = parts[0].trim();
                        String password = parts[1].trim();
                        String watchlist = parts[2].trim();
                        String history = parts[3].trim();

                        User user = new User(username, password, watchlist, history);
                        users.put(username, user);
                    }
                } catch (Exception e) {
                    System.err.println("Warning: Skipping invalid user data line: " + line);
                }
            }
        }

        return users;
    }

    /**
     * 保存用户数据到CSV文件
     * @param users HashMap，键为用户名，值为User对象
     * @throws IOException 如果文件写入失败
     */
    public void saveUsers(HashMap<String, User> users) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(usersFilePath))) {
            // 写入标题行
            writer.println("username,password,watchlist,history");

            // 写入用户数据
            for (User user : users.values()) {
                String username = user.getUsername();
                String password = user.getPassword();
                String watchlist = user.getWatchlist().toString();
                String history = user.getHistory().toString();

                writer.printf("%s,%s,%s,%s%n", username, password, watchlist, history);
            }
        }
    }

    /**
     * 解析CSV行，处理可能包含逗号的字段
     * @param line CSV行
     * @return 字段数组
     */
    private String[] parseCSVLine(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        fields.add(currentField.toString());

        return fields.toArray(new String[0]);
    }
}

