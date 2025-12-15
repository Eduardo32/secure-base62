package com.pauloeduardocosta.securebase62;

import com.pauloeduardocosta.securebase62.exceptions.SecureBase62Exception;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * SecureBase62 - A library for Base62 encoding and decoding with a secret key.
 * The secret key is used to shuffle the Base62 alphabet, making the encoding unique for each key.
 * A secret key must be provided either explicitly or through configuration properties.
 */
public class SecureBase62 {

    private static final String DEFAULT_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String CONFIG_PROPERTY_NAME = "securebase62.secret.key";

    private final int base;
    private final String secretKey;
    private final String shuffledAlphabet;

    /**
     * Creates a new instance of SecureBase62 with the provided secret key.
     *
     * @param secretKey The secret key used to shuffle the alphabet
     * @throws IllegalArgumentException if the secret key is null or empty
     */
    public SecureBase62(final String secretKey) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("Secret key cannot be null or empty");
        }
        this.secretKey = secretKey;
        this.shuffledAlphabet = shuffleAlphabet(DEFAULT_ALPHABET, secretKey);
        this.base = this.shuffledAlphabet.length();
    }

    /**
     * Creates a new instance of SecureBase62 with the secret key loaded from properties.
     * First tries to load from the specified properties file path.
     * If not found, falls back to system properties.
     *
     * @param propertiesPath Path to the properties file (can be null to skip file loading)
     * @throws SecureBase62Exception if no secret key is found in properties or system properties
     */
    public SecureBase62(final String propertiesPath, final boolean loadFromProperties) {
        String key = null;

        if (loadFromProperties) {
            // Try to load from properties file if path is provided
            if (propertiesPath != null && !propertiesPath.isEmpty()) {
                key = loadKeyFromPropertiesFile(propertiesPath);
            }

            // If key is still null, try system properties
            if (key == null) {
                key = System.getProperty(CONFIG_PROPERTY_NAME);
            }

            // If key is still null, throw an exception
            if (key == null || key.isEmpty()) {
                throw new SecureBase62Exception(
                        "No secret key found. Please set the property '" + CONFIG_PROPERTY_NAME +
                                "' in your properties file or as a system property."
                );
            }
        } else {
            throw new SecureBase62Exception(
                    "loadFromProperties must be true when using this constructor"
            );
        }

        this.secretKey = key;
        this.shuffledAlphabet = shuffleAlphabet(DEFAULT_ALPHABET, this.secretKey);
        this.base = this.shuffledAlphabet.length();
    }

    /**
     * Creates an instance using configuration from system properties.
     *
     * @throws SecureBase62Exception if no secret key is found in system properties
     */
    public SecureBase62() {
        String key = System.getProperty(CONFIG_PROPERTY_NAME);

        if (key == null || key.isEmpty()) {
            throw new SecureBase62Exception(
                    "No secret key found. Please set the system property '" + CONFIG_PROPERTY_NAME +
                            "' before creating an instance."
            );
        }

        this.secretKey = key;
        this.shuffledAlphabet = shuffleAlphabet(DEFAULT_ALPHABET, this.secretKey);
        this.base = this.shuffledAlphabet.length();
    }

    /**
     * Loads the secret key from a properties file.
     *
     * @param propertiesPath Path to the properties file
     * @return The secret key from the file or null if not found
     * @throws SecureBase62Exception if there's an error loading the properties file
     */
    private String loadKeyFromPropertiesFile(final String propertiesPath) {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(propertiesPath)) {
            if (input != null) {
                props.load(input);
                return props.getProperty(CONFIG_PROPERTY_NAME);
            } else {
                throw new SecureBase62Exception("Properties file not found: " + propertiesPath);
            }
        } catch (IOException e) {
            throw new SecureBase62Exception("Error loading properties file: " + propertiesPath, e);
        }
    }

    /**
     * Encodes a string to Base62 using the shuffled alphabet.
     *
     * @param input The string to be encoded
     * @return The Base62 encoded string
     */
    public String encode(final String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Convert string to bytes
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);

        // Convert bytes to a large number
        BigInteger number = new BigInteger(1, bytes);

        // Encode the number
        return encodeNumber(number);
    }

    /**
     * Encodes a long number to Base62 using the shuffled alphabet.
     *
     * @param input The long number to be encoded
     * @return The Base62 encoded string
     * @throws IllegalArgumentException if input is null
     */
    public String encode(final Long input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        // Convert Long to BigInteger
        BigInteger number = BigInteger.valueOf(input);

        // Encode the number
        return encodeNumber(number);
    }

    /**
     * Encodes an integer to Base62 using the shuffled alphabet.
     *
     * @param input The integer to be encoded
     * @return The Base62 encoded string
     * @throws IllegalArgumentException if input is null
     */
    public String encode(final Integer input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        // Convert Integer to BigInteger
        BigInteger number = BigInteger.valueOf(input);

        // Encode the number
        return encodeNumber(number);
    }

    /**
     * Encodes a BigInteger to Base62 using the shuffled alphabet.
     *
     * @param number The BigInteger to be encoded
     * @return The Base62 encoded string
     */
    private String encodeNumber(BigInteger number) {
        if (number.compareTo(BigInteger.ZERO) == 0) {
            // Special case for zero
            return String.valueOf(shuffledAlphabet.charAt(0));
        }

        StringBuilder result = new StringBuilder();

        // Handle negative numbers by using a prefix character
        boolean negative = number.compareTo(BigInteger.ZERO) < 0;
        if (negative) {
            number = number.abs();
        }

        // Convert the number to base 62
        while (number.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divmod = number.divideAndRemainder(BigInteger.valueOf(base));
            number = divmod[0];
            int remainder = divmod[1].intValue();
            result.insert(0, shuffledAlphabet.charAt(remainder));
        }

        // Add a prefix for negative numbers (using the last character in the alphabet as a marker)
        if (negative) {
            result.insert(0, shuffledAlphabet.charAt(base - 1));
        }

        return result.toString();
    }

    /**
     * Decodes a Base62 string to the original string using the shuffled alphabet.
     *
     * @param encoded The Base62 encoded string
     * @return The original decoded string
     * @throws IllegalArgumentException If the string contains invalid characters
     */
    public String decode(final String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return "";
        }

        BigInteger number = decodeToBigInteger(encoded);

        return new String(number.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Decodes a Base62 string to a long number using the shuffled alphabet.
     *
     * @param encoded The Base62 encoded string
     * @return The original long number
     * @throws IllegalArgumentException If the string contains invalid characters
     * @throws ArithmeticException If the decoded value exceeds the range of a long
     */
    public Long decodeLong(final String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            throw new IllegalArgumentException("Encoded string cannot be null or empty");
        }

        BigInteger number = decodeToBigInteger(encoded);

        // Check if the number fits in a Long
        if (number.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0 ||
                number.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0) {
            throw new ArithmeticException("Decoded value exceeds the range of a long");
        }

        return number.longValue();
    }

    /**
     * Decodes a Base62 string to an integer number using the shuffled alphabet.
     *
     * @param encoded The Base62 encoded string
     * @return The original integer number
     * @throws IllegalArgumentException If the string contains invalid characters
     * @throws ArithmeticException If the decoded value exceeds the range of an integer
     */
    public Integer decodeInteger(final String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            throw new IllegalArgumentException("Encoded string cannot be null or empty");
        }

        BigInteger number = decodeToBigInteger(encoded);

        // Check if the number fits in an Integer
        if (number.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0 ||
                number.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0) {
            throw new ArithmeticException("Decoded value exceeds the range of an integer");
        }

        return number.intValue();
    }

    /**
     * Helper method to decode a Base62 string to a BigInteger.
     *
     * @param encoded The Base62 encoded string
     * @return The decoded BigInteger
     * @throws IllegalArgumentException If the string contains invalid characters
     */
    private BigInteger decodeToBigInteger(String encoded) {
        BigInteger number = BigInteger.ZERO;
        boolean negative = false;

        // Check if the first character indicates a negative number
        if (!encoded.isEmpty() && encoded.charAt(0) == shuffledAlphabet.charAt(base - 1)) {
            negative = true;
            encoded = encoded.substring(1); // Remove the negative marker
        }

        for (char c : encoded.toCharArray()) {
            number = number.multiply(BigInteger.valueOf(base));
            int index = shuffledAlphabet.indexOf(c);
            if (index == -1) {
                throw new IllegalArgumentException("Invalid Base62 character: " + c);
            }
            number = number.add(BigInteger.valueOf(index));
        }

        // Apply negative sign if needed
        if (negative) {
            number = number.negate();
        }

        return number;
    }

    /**
     * Returns the shuffled alphabet used by this instance.
     *
     * @return The shuffled Base62 alphabet
     */
    public String getShuffledAlphabet() {
        return shuffledAlphabet;
    }

    /**
     * Gets the property name used for configuration.
     *
     * @return The property name
     */
    public static String getConfigPropertyName() {
        return CONFIG_PROPERTY_NAME;
    }

    /**
     * Shuffles an alphabet based on a secret key.
     *
     * @param alphabet The alphabet to be shuffled
     * @param secretKey The secret key to determine the shuffling
     * @return The shuffled alphabet
     */
    private static String shuffleAlphabet(final String alphabet, final String secretKey) {
        try {
            // Convert the string to a list of characters
            List<Character> characters = new ArrayList<>();
            for (char c : alphabet.toCharArray()) {
                characters.add(c);
            }

            // Generate hash of the secret key
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(secretKey.getBytes(StandardCharsets.UTF_8));

            // Use the hash to shuffle the list
            for (int i = characters.size() - 1; i > 0; i--) {
                // Use hash bytes as seed for shuffling
                int index = Math.abs(hashBytes[i % hashBytes.length]) % (i + 1);

                // Swap elements
                Character temp = characters.get(index);
                characters.set(index, characters.get(i));
                characters.set(i, temp);
            }

            // Convert the list back to string
            StringBuilder result = new StringBuilder(characters.size());
            for (Character c : characters) {
                result.append(c);
            }

            return result.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash for shuffling", e);
        }
    }
}
