package dev.valhal.hytale.plugin.util;

/**
 * Utility methods for JSON string handling.
 */
public final class JsonUtils {

    private JsonUtils() {}

    /**
     * Escapes special characters in a string for use in JSON.
     */
    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    /**
     * Wraps a string value in quotes with proper escaping for JSON.
     * Returns "null" (the JSON literal) for null input.
     */
    public static String quote(String value) {
        if (value == null) {
            return "null";
        }
        return "\"" + escape(value) + "\"";
    }

    /**
     * Returns the value or empty string if null.
     */
    public static String nullSafe(String value) {
        return value != null ? value : "";
    }
}
