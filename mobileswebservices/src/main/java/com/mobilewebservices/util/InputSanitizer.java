package com.mobilewebservices.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class InputSanitizer {

    // Pattern to detect potential XSS attacks
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)<script[^>]*>.*?</script>|javascript:|on\\w+\\s*=|<iframe|<object|<embed|<link|<meta",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL
    );

    // Pattern to detect SQL injection attempts
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute)\\s+",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Sanitize string input to prevent XSS attacks
     */
    public String sanitizeForXSS(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        // Remove potential XSS patterns
        String sanitized = input.replaceAll("<script[^>]*>.*?</script>", "");
        sanitized = sanitized.replaceAll("(?i)javascript:", "");
        sanitized = sanitized.replaceAll("(?i)on\\w+\\s*=", "");

        // Encode HTML entities
        sanitized = sanitized.replace("<", "&lt;")
                            .replace(">", "&gt;")
                            .replace("\"", "&quot;")
                            .replace("'", "&#x27;")
                            .replace("/", "&#x2F;");

        return sanitized.trim();
    }

    /**
     * Basic validation for SQL injection attempts
     */
    public boolean containsSQLInjection(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        return SQL_INJECTION_PATTERN.matcher(input).find();
    }

    /**
     * Basic validation for XSS attempts
     */
    public boolean containsXSS(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        return XSS_PATTERN.matcher(input).find();
    }

    /**
     * Validate and sanitize string input
     */
    public String validateAndSanitize(String input) throws IllegalArgumentException {
        if (input == null) {
            return null;
        }

        if (containsXSS(input)) {
            throw new IllegalArgumentException("Input contains potentially malicious content (XSS)");
        }

        if (containsSQLInjection(input)) {
            throw new IllegalArgumentException("Input contains potentially malicious content (SQL Injection)");
        }

        return sanitizeForXSS(input);
    }

    /**
     * Clean string for safe database storage
     */
    public String sanitizeForDatabase(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        // Basic cleaning - remove excessive whitespace, control characters
        String sanitized = input.replaceAll("\\p{Cntrl}", "") // Remove control characters
                               .replaceAll("\\s+", " ")        // Replace multiple whitespace with single space
                               .trim();

        return validateAndSanitize(sanitized);
    }
}
