package com.mobilewebservices.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility class for handling HTML content - decoding HTML entities and stripping tags
 */
@Component
public class HtmlUtils {

    private static final Map<String, String> HTML_ENTITIES = new HashMap<>();
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");

    static {
        // Common HTML entities
        HTML_ENTITIES.put("&lt;", "<");
        HTML_ENTITIES.put("&gt;", ">");
        HTML_ENTITIES.put("&amp;", "&");
        HTML_ENTITIES.put("&quot;", "\"");
        HTML_ENTITIES.put("&#x27;", "'");
        HTML_ENTITIES.put("&#x2F;", "/");
        HTML_ENTITIES.put("&apos;", "'");
        HTML_ENTITIES.put("&nbsp;", " ");
        HTML_ENTITIES.put("&copy;", "©");
        HTML_ENTITIES.put("&reg;", "®");
        HTML_ENTITIES.put("&trade;", "™");
        HTML_ENTITIES.put("&euro;", "€");
        HTML_ENTITIES.put("&pound;", "£");
        HTML_ENTITIES.put("&yen;", "¥");
        HTML_ENTITIES.put("&cent;", "¢");
        HTML_ENTITIES.put("&sect;", "§");
        HTML_ENTITIES.put("&para;", "¶");
        HTML_ENTITIES.put("&middot;", "·");
        HTML_ENTITIES.put("&bull;", "•");
        HTML_ENTITIES.put("&hellip;", "…");
        HTML_ENTITIES.put("&prime;", "′");
        HTML_ENTITIES.put("&Prime;", "″");
        HTML_ENTITIES.put("&lsaquo;", "‹");
        HTML_ENTITIES.put("&rsaquo;", "›");
        HTML_ENTITIES.put("&laquo;", "«");
        HTML_ENTITIES.put("&raquo;", "»");
        HTML_ENTITIES.put("&ndash;", "–");
        HTML_ENTITIES.put("&mdash;", "—");
        HTML_ENTITIES.put("&iquest;", "¿");
        HTML_ENTITIES.put("&iexcl;", "¡");
        HTML_ENTITIES.put("&deg;", "°");
        HTML_ENTITIES.put("&plusmn;", "±");
        HTML_ENTITIES.put("&times;", "×");
        HTML_ENTITIES.put("&divide;", "÷");
        HTML_ENTITIES.put("&frac14;", "¼");
        HTML_ENTITIES.put("&frac12;", "½");
        HTML_ENTITIES.put("&frac34;", "¾");

        // Turkish characters
        HTML_ENTITIES.put("&Ccedil;", "Ç");
        HTML_ENTITIES.put("&ccedil;", "ç");
        HTML_ENTITIES.put("&Ouml;", "Ö");
        HTML_ENTITIES.put("&ouml;", "ö");
        HTML_ENTITIES.put("&Uuml;", "Ü");
        HTML_ENTITIES.put("&uuml;", "ü");
        HTML_ENTITIES.put("&Icirc;", "İ");
        HTML_ENTITIES.put("&icirc;", "î");
        HTML_ENTITIES.put("&#304;", "İ");
        HTML_ENTITIES.put("&#305;", "ı");
        HTML_ENTITIES.put("&Gbreve;", "Ğ");
        HTML_ENTITIES.put("&gbreve;", "ğ");
        HTML_ENTITIES.put("&#286;", "Ğ");
        HTML_ENTITIES.put("&#287;", "ğ");
        HTML_ENTITIES.put("&Scedil;", "Ş");
        HTML_ENTITIES.put("&scedil;", "ş");
        HTML_ENTITIES.put("&#350;", "Ş");
        HTML_ENTITIES.put("&#351;", "ş");
    }

    /**
     * Decode HTML entities in the input string
     */
    public String decodeHtmlEntities(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String result = input;

        // Decode named entities
        for (Map.Entry<String, String> entry : HTML_ENTITIES.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }

        // Decode numeric entities (&#xxx; and &#xHHH;)
        result = decodeNumericEntities(result);

        return result;
    }

    /**
     * Decode numeric HTML entities (&#xxx; and &#xHHH;)
     */
    private String decodeNumericEntities(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < input.length()) {
            if (input.charAt(i) == '&' && i + 2 < input.length() && input.charAt(i + 1) == '#') {
                int semicolonIndex = input.indexOf(';', i + 2);
                if (semicolonIndex > i + 2 && semicolonIndex - i < 10) {
                    try {
                        String entityContent = input.substring(i + 2, semicolonIndex);
                        int codePoint;

                        if (entityContent.startsWith("x") || entityContent.startsWith("X")) {
                            // Hexadecimal entity
                            codePoint = Integer.parseInt(entityContent.substring(1), 16);
                        } else {
                            // Decimal entity
                            codePoint = Integer.parseInt(entityContent);
                        }

                        result.append((char) codePoint);
                        i = semicolonIndex + 1;
                        continue;
                    } catch (NumberFormatException e) {
                        // Invalid entity, keep as is
                    }
                }
            }
            result.append(input.charAt(i));
            i++;
        }

        return result.toString();
    }

    /**
     * Strip HTML tags from the input string
     */
    public String stripHtmlTags(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Remove HTML tags
        String result = HTML_TAG_PATTERN.matcher(input).replaceAll("");

        // Clean up excessive whitespace
        result = result.replaceAll("\\s+", " ").trim();

        return result;
    }

    /**
     * Decode HTML entities and strip HTML tags
     */
    public String decodeAndStripHtml(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // First strip tags, then decode entities
        String result = stripHtmlTags(input);
        result = decodeHtmlEntities(result);

        return result;
    }

    /**
     * Clean text for plain text output (decode entities but keep structure)
     */
    public String cleanTextForOutput(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Decode HTML entities first
        String result = decodeHtmlEntities(input);

        // Replace common HTML tags with plain text equivalents
        result = result.replaceAll("(?i)<br\\s*/?>", "\n");
        result = result.replaceAll("(?i)<p[^>]*>", "\n");
        result = result.replaceAll("(?i)</p>", "\n");
        result = result.replaceAll("(?i)<li[^>]*>", "\n• ");
        result = result.replaceAll("(?i)</li>", "");

        // Strip remaining HTML tags
        result = stripHtmlTags(result);

        // Clean up excessive newlines and whitespace
        result = result.replaceAll("\n{3,}", "\n\n");
        result = result.replaceAll("[ \\t]+", " ");
        result = result.trim();

        return result;
    }
}
