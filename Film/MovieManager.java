import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

/**
 * MovieManager 电影管理，负责电影数据加载、存储和查找
 */
public class MovieManager {
    private ArrayList<Movie> movies = new ArrayList<>();
    private HashMap<String, Movie> movieMap = new HashMap<>();

    /**
     * 从CSV文件加载所有电影
     */
    public void loadMovies(String filename) {
        movies.clear();
        movieMap.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine(); // 跳过表头
            while ((line = br.readLine()) != null) {
                String[] arr = line.split(",",-1);
                if (arr.length == 5) {
                    String id = arr[0].trim();
                    String title = arr[1].trim();
                    String genre = arr[2].trim();
                    int year = Integer.parseInt(arr[3].trim());
                    double rating = Double.parseDouble(arr[4].trim());
                    Movie m = new Movie(id, title, genre, year, rating);
                    movies.add(m);
                    movieMap.put(id, m);
                }
            }
        } catch (Exception e) {
            System.out.println("加载电影文件失败: " + e.getMessage());
        }
    }

    /**
     * 根据电影ID查找电影
     */
    public Movie getMovieById(String id) {
        return movieMap.get(id);
    }

    /**
     * 返回所有电影列表
     */
    public ArrayList<Movie> getAllMovies() {
        return movies;
    }
}
