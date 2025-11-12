/**
 * 电影实体类，包含电影的基本信息
 */
public class Movie {
    private String id;        // 电影ID
    private String title;     // 电影名
    private String genre;     // 电影类型
    private int year;         // 上映年份
    private double rating;    // 评分

    /**
     * Movie 构造方法
     */
    public Movie(String id, String title, String genre, int year, double rating) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.rating = rating;
    }

    // getter 方法
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getYear() { return year; }
    public double getRating() { return rating; }

    @Override
    public String toString() {
        return id + " - " + title + " (" + year + ", " + genre + ", " + rating + ")";
    }
}
