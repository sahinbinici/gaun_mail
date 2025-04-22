package gaun.apply.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomPasswordGenerator {
    public static String rastgeleSifreUret(int uzunluk) {

        String numbers = "0123456789";
        String specCharacters = "!@#.,*-_+";
        String myChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        String allCharacters = numbers + specCharacters + myChars;

        SecureRandom random = new SecureRandom();
        List<Character> sifreKarakterleri = new ArrayList<>();

        // En az 1 rakam
        sifreKarakterleri.add(numbers.charAt(random.nextInt(numbers.length())));

        // En az 1 özel karakter
        sifreKarakterleri.add(specCharacters.charAt(random.nextInt(specCharacters.length())));

        // Geriye kalan karakterleri rastgele doldur
        for (int i = 2; i < uzunluk; i++) {
            sifreKarakterleri.add(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }

        // Karakterleri karıştır
        Collections.shuffle(sifreKarakterleri, random);

        // Listeyi stringe çevir
        StringBuilder sifre = new StringBuilder();
        for (char c : sifreKarakterleri) {
            sifre.append(c);
        }

        return sifre.toString();
    }
}
