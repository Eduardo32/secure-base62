package com.pauloeduardocosta.securebase62.utils;

import com.pauloeduardocosta.securebase62.SecureBase62;
import com.pauloeduardocosta.securebase62.exceptions.SecureBase62Exception;

/**
 * Utility class for common operations with SecureBase62.
 * Requires the system property 'securebase62.secret.key' to be set.
 */
public class SecureBase62Utils {

    private static SecureBase62 defaultInstance = null;

    private SecureBase62Utils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Gets the default instance, initializing it if necessary.
     * The default instance uses the system property for configuration.
     *
     * @return The default SecureBase62 instance
     * @throws SecureBase62Exception if the required system property is not set
     */
    private static synchronized SecureBase62 getDefaultInstance() {
        if (defaultInstance == null) {
            defaultInstance = new SecureBase62();
        }
        return defaultInstance;
    }

    /**
     * Encodes a string to Base62 using the default instance.
     *
     * @param input The string to be encoded
     * @return The Base62 encoded string
     * @throws SecureBase62Exception if the required system property is not set
     */
    public static String encode(final String input) {
        return getDefaultInstance().encode(input);
    }

    /**
     * Encodes a long number to Base62 using the default instance.
     *
     * @param input The long number to be encoded
     * @return The Base62 encoded string
     * @throws SecureBase62Exception if the required system property is not set
     */
    public static String encode(final Long input) {
        return getDefaultInstance().encode(input);
    }

    /**
     * Encodes an integer number to Base62 using the default instance.
     *
     * @param input The integer number to be encoded
     * @return The Base62 encoded string
     * @throws SecureBase62Exception if the required system property is not set
     */
    public static String encode(final Integer input) {
        return getDefaultInstance().encode(input);
    }

    /**
     * Decodes a Base62 string to the original string using the default instance.
     *
     * @param encoded The Base62 encoded string
     * @return The original decoded string
     * @throws SecureBase62Exception if the required system property is not set
     */
    public static String decode(final String encoded) {
        return getDefaultInstance().decode(encoded);
    }

    /**
     * Decodes a Base62 string to a long number using the default instance.
     *
     * @param encoded The Base62 encoded string
     * @return The original long number
     * @throws SecureBase62Exception if the required system property is not set
     * @throws IllegalArgumentException If the string contains invalid characters
     * @throws ArithmeticException If the decoded value exceeds the range of a long
     */
    public static Long decodeLong(final String encoded) {
        return getDefaultInstance().decodeLong(encoded);
    }

    /**
     * Decodes a Base62 string to an integer number using the default instance.
     *
     * @param encoded The Base62 encoded string
     * @return The original integer number
     * @throws SecureBase62Exception if the required system property is not set
     * @throws IllegalArgumentException If the string contains invalid characters
     * @throws ArithmeticException If the decoded value exceeds the range of an integer
     */
    public static Integer decodeInteger(final String encoded) {
        return getDefaultInstance().decodeInteger(encoded);
    }

    /**
     * Generates a random string for use as a secret key.
     *
     * @param length The length of the random string
     * @return A random string
     * @throws IllegalArgumentException if length is less than or equal to zero
     */
    public static String generateRandomKey(final int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than zero");
        }

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        StringBuilder sb = new StringBuilder(length);
        java.security.SecureRandom random = new java.security.SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }

        return sb.toString();
    }

    /**
     * Resets the default instance, forcing it to reload configuration.
     * Useful after changing system properties.
     */
    public static synchronized void resetDefaultInstance() {
        defaultInstance = null;
    }

    /**
     * Checks if the required system property is set.
     *
     * @return true if the property is set, false otherwise
     */
    public static boolean isConfigured() {
        String key = System.getProperty(SecureBase62.getConfigPropertyName());
        return key != null && !key.isEmpty();
    }
}
