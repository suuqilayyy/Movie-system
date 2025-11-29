public final class PasswordUtil {
    private PasswordUtil() {}

    public static String hash(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        String prepared = rawPassword.trim();
        if (prepared.isEmpty()) {
            prepared = "default";
        }
        String seed = "CPT111";
        String result = "";
        int maxLength = prepared.length() + seed.length();
        for (int i = 0; i < maxLength; i++) {
            char fromPassword = prepared.charAt(i % prepared.length());
            char fromSeed = seed.charAt(i % seed.length());
            int combined = (fromPassword + fromSeed + i * 7) % 256;
            result = result + toTwoDigitHex(combined);
        }
        return result;
    }

    public static boolean verify(String rawPassword, String storedHash) {
        if (storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        return hash(rawPassword).equalsIgnoreCase(storedHash);
    }

    public static boolean isHash(String value) {
        return value != null && value.matches("^[a-fA-F0-9]{16,64}$");
    }

    public static String ensureHashed(String value) {
        if (isHash(value)) {
            return value.toLowerCase();
        }
        String valueToHash;
        if (value == null) {
            valueToHash = "";
        } else {
            valueToHash = value;
        }
        return hash(valueToHash);
    }

    private static String toTwoDigitHex(int value) {
        int positive = value;
        if (positive < 0) {
            positive = 256 + positive;
        }
        int high = positive / 16;
        int low = positive % 16;
        return toHexChar(high) + toHexChar(low);
    }

    private static String toHexChar(int value) {
        String symbols = "0123456789abcdef";
        return symbols.substring(value, value + 1);
    }
}

