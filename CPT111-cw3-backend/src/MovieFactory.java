public final class MovieFactory {
    private MovieFactory() {}

    public static Movie createMovie(String id, String title, String genre, int year, double rating) {
        String category = determineCategory(genre, year);
        if (MovieCategory.SHORT_FILM.equals(category)) {
            return new ShortFilm(id, title, genre, year, rating);
        }
        return new FeatureFilm(id, title, genre, year, rating);
    }

    private static String determineCategory(String genre, int year) {
        if ("ANIMATION".equalsIgnoreCase(genre) || year < 1980) {
            return MovieCategory.SHORT_FILM;
        }
        return MovieCategory.FEATURE_FILM;
    }
}

