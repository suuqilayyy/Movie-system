/**
 * Movie类表示一部电影
 * 包含电影的ID、标题、类型、年份和评分
 */
public class Movie {
    private String id;
    private String title;
    private String genre;
    private int year;
    private double rating;

    /**
     * 构造函数
     * @param id 电影ID
     * @param title 电影标题
     * @param genre 电影类型
     * @param year 上映年份
     * @param rating 评分（0.0-10.0）
     */
    public Movie(String id, String title, String genre, int year, double rating) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.rating = rating;
    }

    // Getter方法
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    public double getRating() {
        return rating;
    }

    /**
     * 返回电影的字符串表示
     * @return 格式化的电影信息字符串
     */
    @Override
    public String toString() {
        return String.format("%s - %s (%d) [%s] Rating: %.1f", 
                           id, title, year, genre, rating);
    }

    /**
     * 检查两个Movie对象是否相等（基于ID）
     * @param obj 要比较的对象
     * @return 如果ID相同返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Movie movie = (Movie) obj;
        return id != null && id.equals(movie.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

