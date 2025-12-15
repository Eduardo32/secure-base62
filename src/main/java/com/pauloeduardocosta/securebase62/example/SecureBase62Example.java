package com.pauloeduardocosta.securebase62.example;

import com.pauloeduardocosta.securebase62.SecureBase62;
import com.pauloeduardocosta.securebase62.utils.SecureBase62Utils;

/**
 * Example usage of the SecureBase62 library.
 */
public class SecureBase62Example {

    public static void main(String[] args) {
        try {
            // Set a system property for examples
            System.setProperty(SecureBase62.getConfigPropertyName(), "example_key_123");

            // Create an instance with an explicit secret key
            SecureBase62 secureBase62 = new SecureBase62("my_secret_key_123");

            // Example 1: String encoding
            System.out.println("Example 1: String encoding");
            demoStringEncoding(secureBase62);

            // Example 2: Long encoding
            System.out.println("\nExample 2: Long encoding");
            demoLongEncoding(secureBase62);

            // Example 3: Integer encoding
            System.out.println("\nExample 3: Integer encoding");
            demoIntegerEncoding(secureBase62);

            // Example 4: Comparing Integer and Long encodings
            System.out.println("\nExample 4: Comparing Integer and Long encodings");
            compareIntegerAndLongEncodings(secureBase62);

            // Example 5: Using the utility class
            System.out.println("\nExample 5: Using utility class");
            demoUtilityClass();

            // Example 6: Encoding database IDs
            System.out.println("\nExample 6: Encoding database IDs");
            demoDatabaseIds(secureBase62);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void demoStringEncoding(final SecureBase62 encoder) {
        String original = "Hello World!";
        String encoded = encoder.encode(original);
        String decoded = encoder.decode(encoded);

        System.out.println("Shuffled alphabet: " + encoder.getShuffledAlphabet());
        System.out.println("Original string: " + original);
        System.out.println("Encoded: " + encoded);
        System.out.println("Decoded: " + decoded);
    }

    private static void demoLongEncoding(final SecureBase62 encoder) {
        Long original = 9876543210L;
        String encoded = encoder.encode(original);
        Long decoded = encoder.decodeLong(encoded);

        System.out.println("Original Long: " + original);
        System.out.println("Encoded: " + encoded);
        System.out.println("Decoded Long: " + decoded);
        System.out.println("Matched: " + original.equals(decoded));
    }

    private static void demoIntegerEncoding(final SecureBase62 encoder) {
        Integer original = 123456;
        String encoded = encoder.encode(original);
        Integer decoded = encoder.decodeInteger(encoded);

        System.out.println("Original Integer: " + original);
        System.out.println("Encoded: " + encoded);
        System.out.println("Decoded Integer: " + decoded);
        System.out.println("Matched: " + original.equals(decoded));
    }

    private static void compareIntegerAndLongEncodings(final SecureBase62 encoder) {
        int value = 12345;

        // Encode as Integer
        String encodedInt = encoder.encode(value);

        // Encode as Long
        String encodedLong = encoder.encode((long)value);

        System.out.println("Value: " + value);
        System.out.println("Encoded as Integer: " + encodedInt);
        System.out.println("Encoded as Long: " + encodedLong);
        System.out.println("Same encoding: " + encodedInt.equals(encodedLong));

        // Decode both ways
        Integer decodedInt = encoder.decodeInteger(encodedInt);
        Long decodedLong = encoder.decodeLong(encodedLong);

        System.out.println("Decoded Integer: " + decodedInt);
        System.out.println("Decoded Long: " + decodedLong);
    }

    private static void demoUtilityClass() {
        // Using utility class with different types
        String stringResult = SecureBase62Utils.encode("Hello from Utils!");
        String longResult = SecureBase62Utils.encode(123456789L);
        String intResult = SecureBase62Utils.encode(123456);

        System.out.println("String encoding: " + stringResult);
        System.out.println("Long encoding: " + longResult);
        System.out.println("Integer encoding: " + intResult);

        // Decoding
        String decodedString = SecureBase62Utils.decode(stringResult);
        Long decodedLong = SecureBase62Utils.decodeLong(longResult);
        Integer decodedInt = SecureBase62Utils.decodeInteger(intResult);

        System.out.println("Decoded string: " + decodedString);
        System.out.println("Decoded long: " + decodedLong);
        System.out.println("Decoded integer: " + decodedInt);
    }

    private static void demoDatabaseIds(final SecureBase62 encoder) {
        System.out.println("Integer IDs (typical for smaller systems):");
        for (int id = 1; id <= 5; id++) {
            String encoded = encoder.encode(id);
            System.out.println("ID " + id + " → " + encoded);
        }

        System.out.println("\nLong IDs (typical for larger systems):");
        for (long id = 1000000000L; id <= 1000000005L; id++) {
            String encoded = encoder.encode(id);
            System.out.println("ID " + id + " → " + encoded);
        }
    }
}
