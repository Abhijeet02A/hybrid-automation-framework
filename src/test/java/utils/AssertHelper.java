package utils;

import org.junit.jupiter.api.Assertions;

public class AssertHelper {

    public void equals(String field, Object expected, Object actual) {
        Assertions.assertEquals(
                expected,
                actual,
                String.format("Assertion failed for [%s]. Expected: <%s>, Actual: <%s>", field, expected, actual));
    }

    public void isTrue(String field, boolean condition) {
        Assertions.assertTrue(
                condition,
                String.format("Assertion failed for [%s]. Expected true but was false.", field));
    }

    public void notNull(String field, Object value) {
        Assertions.assertNotNull(
                value,
                String.format("Assertion failed for [%s]. Value is null.", field));
    }
}
