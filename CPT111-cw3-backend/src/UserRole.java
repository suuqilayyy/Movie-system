public final class UserRole {
    public static final String BASIC = "BASIC";
    public static final String PREMIUM = "PREMIUM";

    private UserRole() {}

    public static String fromString(String value) {
        if (value == null) {
            return BASIC;
        }
        String trimmed = value.trim().toUpperCase();
        if (PREMIUM.equals(trimmed)) {
            return PREMIUM;
        }
        return BASIC;
    }

    public static boolean isPremium(String roleValue) {
        if (roleValue == null) {
            return false;
        }
        return PREMIUM.equals(roleValue.trim().toUpperCase());
    }
}

