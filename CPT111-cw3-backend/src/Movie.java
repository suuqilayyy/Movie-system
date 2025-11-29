public class Movie {
    private final String id;
    private final String title;
    private final String genre;
    private final int year;
    private final double rating;
    private final String category;

    protected Movie(String id, String title, String genre, int year, double rating, String categoryValue) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.rating = rating;
        if (categoryValue == null) {
            this.category = MovieCategory.FEATURE_FILM;
        } else {
            this.category = categoryValue;
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getYear() { return year; }
    public double getRating() { return rating; }
    public String getCategory() { return category; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%d) - %s | %s [Rating: %.1f]",
                id, title, year, genre, category, rating);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        if (id == null && movie.id == null) {
            return true;
        }
        if (id == null || movie.id == null) {
            return false;
        }
        return id.equals(movie.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return 0;
        } else {
            return id.hashCode();
        }
    }
}