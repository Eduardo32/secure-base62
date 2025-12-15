package com.pauloeduardocosta.securebase62;

import com.pauloeduardocosta.securebase62.exceptions.SecureBase62Exception;
import com.pauloeduardocosta.securebase62.utils.SecureBase62Utils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SecureBase62Test {

    @Before
    public void setUp() {
        // Clear any system properties before each test
        System.clearProperty(SecureBase62.getConfigPropertyName());
        SecureBase62Utils.resetDefaultInstance();
    }

    @Test
    public void testEncodeAndDecodeString() {
        SecureBase62 secureBase62 = new SecureBase62("test-key-123");
        String original = "Hello World!";
        String encoded = secureBase62.encode(original);
        String decoded = secureBase62.decode(encoded);

        assertEquals(original, decoded);
    }

    @Test
    public void testEncodeAndDecodeLong() {
        SecureBase62 secureBase62 = new SecureBase62("test-key-123");
        Long original = 12345678901234L;
        String encoded = secureBase62.encode(original);
        Long decoded = secureBase62.decodeLong(encoded);

        assertEquals(original, decoded);
    }

    @Test
    public void testEncodeAndDecodeInteger() {
        SecureBase62 secureBase62 = new SecureBase62("test-key-123");
        Integer original = 123456;
        String encoded = secureBase62.encode(original);
        Integer decoded = secureBase62.decodeInteger(encoded);

        assertEquals(original, decoded);
    }

    @Test
    public void testEncodeZero() {
        SecureBase62 secureBase62 = new SecureBase62("test-key-123");
        // Test with Long
        Long originalLong = 0L;
        String encodedLong = secureBase62.encode(originalLong);
        Long decodedLong = secureBase62.decodeLong(encodedLong);
        assertEquals(originalLong, decodedLong);

        // Test with Integer
        Integer originalInt = 0;
        String encodedInt = secureBase62.encode(originalInt);
        Integer decodedInt = secureBase62.decodeInteger(encodedInt);
        assertEquals(originalInt, decodedInt);
    }

    @Test
    public void testEncodeNegativeNumbers() {
        SecureBase62 secureBase62 = new SecureBase62("test-key-123");
        // Test with Long
        Long originalLong = -9876543210L;
        String encodedLong = secureBase62.encode(originalLong);
        Long decodedLong = secureBase62.decodeLong(encodedLong);
        assertEquals(originalLong, decodedLong);

        // Test with Integer
        Integer originalInt = -123456;
        String encodedInt = secureBase62.encode(originalInt);
        Integer decodedInt = secureBase62.decodeInteger(encodedInt);
        assertEquals(originalInt, decodedInt);
    }

    @Test
    public void testEncodeLongMaxValue() {
        SecureBase62 secureBase62 = new SecureBase62("test-key-123");
        Long original = Long.MAX_VALUE;
        String encoded = secureBase62.encode(original);
        Long decoded = secureBase62.decodeLong(encoded);

        assertEquals(original, decoded);
    }

    @Test
    public void testEncodeLongMinValue() {
        SecureBase62 secureBase62 = new SecureBase62("test-key-123");
        Long original = Long.MIN_VALUE;
        String encoded = secureBase62.encode(original);
        Long decoded = secureBase62.decodeLong(encoded);

        assertEquals(original, decoded);
    }

    @Test
    public void testEncodeIntegerMaxValue() {
        SecureBase62 secureBase62 = new SecureBase62("test-key-123");
        Integer original = Integer.MAX_VALUE;
        String encoded = secureBase62.encode(original);
        Integer decoded = secureBase62.decodeInteger(encoded);

        assertEquals(original, decoded);
    }

    @Test
    public void testEncodeIntegerMinValue() {
        SecureBase62 secureBase62 = new SecureBase62("test-key-123");
        Integer original = Integer.MIN_VALUE;
        String encoded = secureBase62.encode(original);
        Integer decoded = secureBase62.decodeInteger(encoded);

        assertEquals(original, decoded);
    }

    @Test
    public void testEmptyString() {
        SecureBase62 secureBase62 = new SecureBase62("test-key-123");
        assertEquals("", secureBase62.encode(""));
        assertEquals("", secureBase62.decode(""));
    }

    @Test
    public void testDifferentKeysProduceDifferentResults() {
        SecureBase62 encoder1 = new SecureBase62("key1");
        SecureBase62 encoder2 = new SecureBase62("key2");

        // Test with String
        String originalString = "Same input text";
        String encodedString1 = encoder1.encode(originalString);
        String encodedString2 = encoder2.encode(originalString);
        assertNotEquals(encodedString1, encodedString2);
        assertEquals(originalString, encoder1.decode(encodedString1));
        assertEquals(originalString, encoder2.decode(encodedString2));

        // Test with Long
        Long originalLong = 12345L;
        String encodedLong1 = encoder1.encode(originalLong);
        String encodedLong2 = encoder2.encode(originalLong);
        assertNotEquals(encodedLong1, encodedLong2);
        assertEquals(originalLong, encoder1.decodeLong(encodedLong1));
        assertEquals(originalLong, encoder2.decodeLong(encodedLong2));

        // Test with Integer
        Integer originalInt = 12345;
        String encodedInt1 = encoder1.encode(originalInt);
        String encodedInt2 = encoder2.encode(originalInt);
        assertNotEquals(encodedInt1, encodedInt2);
        assertEquals(originalInt, encoder1.decodeInteger(encodedInt1));
        assertEquals(originalInt, encoder2.decodeInteger(encodedInt2));
    }

    @Test
    public void testSystemPropertyConfiguration() {
        // Set a system property
        System.setProperty(SecureBase62.getConfigPropertyName(), "system-test-key");

        // Create an instance that should use the system property
        SecureBase62 secureBase62 = new SecureBase62();

        // Create another instance with the same key explicitly
        SecureBase62 explicitInstance = new SecureBase62("system-test-key");

        // Both should produce the same encoding
        String original = "Test system property";
        assertEquals(explicitInstance.encode(original), secureBase62.encode(original));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCharacter() {
        SecureBase62 secureBase62 = new SecureBase62("test-key-123");
        secureBase62.decode("Invalid$Character");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullSecretKey() {
        new SecureBase62(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptySecretKey() {
        new SecureBase62("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullLongInput() {
        SecureBase62 secureBase62 = new SecureBase62("test-key-123");
        secureBase62.encode((Long)null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullIntegerInput() {
        SecureBase62 secureBase62 = new SecureBase62("test-key-123");
        secureBase62.encode((Integer)null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyStringForDecodeLong() {
        SecureBase62 secureBase62 = new SecureBase62("test-key-123");
        secureBase62.decodeLong("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyStringForDecodeInteger() {
        SecureBase62 secureBase62 = new SecureBase62("test-key-123");
        secureBase62.decodeInteger("");
    }

    @Test(expected = ArithmeticException.class)
    public void testIntegerOverflow() {
        SecureBase62 secureBase62 = new SecureBase62("test-key-123");
        // Encode a value that's too large for Integer
        String encoded = secureBase62.encode(Long.valueOf(Integer.MAX_VALUE) + 1);
        // This should throw an exception
        secureBase62.decodeInteger(encoded);
    }

    @Test(expected = SecureBase62Exception.class)
    public void testMissingSystemProperty() {
        // This should throw an exception because no system property is set
        new SecureBase62();
    }

    @Test(expected = SecureBase62Exception.class)
    public void testMissingPropertyInFile() {
        // This should throw an exception if the property file doesn't contain the key
        // Note: You would need to create a test properties file without the key
        new SecureBase62("missing-key.properties", true);
    }

    @Test
    public void testIntegerAndLongProduceSameEncoding() {
        SecureBase62 secureBase62 = new SecureBase62("test-key-123");
        int value = 12345;

        String encodedInt = secureBase62.encode(value);
        String encodedLong = secureBase62.encode((long)value);

        assertEquals("Integer and Long with same value should produce same encoding",
                encodedInt, encodedLong);
    }
}
