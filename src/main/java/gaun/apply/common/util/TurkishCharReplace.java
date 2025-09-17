package gaun.apply.common.util;

import java.util.HashMap;
import java.util.Map;

public class TurkishCharReplace {
    private static final Map<Character, Character> TURKISH_CHAR_MAP = new HashMap<>();

    static {
        TURKISH_CHAR_MAP.put('ç', 'c');
        TURKISH_CHAR_MAP.put('Ç', 'C');
        TURKISH_CHAR_MAP.put('ğ', 'g');
        TURKISH_CHAR_MAP.put('Ğ', 'G');
        TURKISH_CHAR_MAP.put('ı', 'i');
        TURKISH_CHAR_MAP.put('I', 'I'); // Büyük I İngilizce'de aynı
        TURKISH_CHAR_MAP.put('İ', 'I');
        TURKISH_CHAR_MAP.put('ö', 'o');
        TURKISH_CHAR_MAP.put('Ö', 'O');
        TURKISH_CHAR_MAP.put('ş', 's');
        TURKISH_CHAR_MAP.put('Ş', 'S');
        TURKISH_CHAR_MAP.put('ü', 'u');
        TURKISH_CHAR_MAP.put('Ü', 'U');
    }

    public static String replaceTurkishChars(StringBuilder sb) {
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            if (TURKISH_CHAR_MAP.containsKey(c)) {
                sb.setCharAt(i, TURKISH_CHAR_MAP.get(c));
            }
        }
        return sb.toString().toLowerCase();
    }

}

