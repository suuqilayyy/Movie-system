public class FeatureFilm extends Movie {
    public FeatureFilm(String id, String title, String genre, int year, double rating) {
        super(id, title, genre, year, rating, MovieCategory.FEATURE_FILM);
    }
}

