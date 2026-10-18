package com.lehighhub;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Local deny-list moderation for free-text fields on the demo club board. */
final class ClubPostModeration {
    private static final Set<String> BLOCKED_TERMS = Set.of(
            "fuck", "fucker", "fuckers", "fucking", "fucked", "fucks", "motherfuck", "motherfucker", "motherfuckers", "motherfucking",
            "shit", "shits", "shitty", "shitting", "bullshit", "horseshit", "batshit", "shithead", "shitshow", "shithole",
            "bitch", "bitches", "bitching", "bitchy", "asshole", "assholes", "asshat", "asswipe", "jackass", "jackasses",
            "bastard", "bastards", "crap", "crappy", "damn", "dick", "dicks", "dickhead", "dickheads", "prick", "pricks",
            "cunt", "cunts", "cock", "cocks", "piss", "pissed", "pissing", "pussy", "pussies", "slut", "sluts",
            "whore", "whores", "twat", "twats", "wanker", "wankers", "arsehole", "arseholes", "bollocks", "bugger",
            "nigger", "niggers", "nigga", "niggas", "chink", "chinks", "gook", "gooks", "spic", "spics", "wetback",
            "wetbacks", "kike", "kikes", "paki", "pakis", "raghead", "ragheads", "towelhead", "towelheads", "faggot",
            "faggots", "fag", "fags", "dyke", "dykes", "tranny", "trannies", "retard", "retards", "retarded",
            "retardation", "coon", "coons", "darkie", "darkies", "jap", "japs", "zipperhead", "zipperheads",
            "hebe", "hebes", "hymie", "hymies", "yid", "yids", "kraut", "krauts", "redskin", "redskins", "dothead", "dotheads",
            "sex", "sexual", "sexually", "sexy", "sext", "sexting", "porn", "porno", "pornography", "pornographic", "xxx",
            "erotic", "erotica", "nude", "nudes", "naked", "topless", "milf", "milfs", "dilf", "dilfs", "penis", "penises",
            "vagina", "vaginas", "vulva", "vulvas", "clitoris", "boob", "boobs", "breast", "breasts", "dildo", "dildos",
            "vibrator", "vibrators", "masturbate", "masturbating", "masturbation", "orgasm", "orgasms", "cum", "cumming",
            "cumshot", "ejaculate", "ejaculation", "blowjob", "blowjobs", "handjob", "handjobs", "hookup", "hookups",
            "onlyfans", "hentai", "stripper", "strippers", "brothel", "brothels", "prostitute", "prostitutes", "escort", "escorts");

    private ClubPostModeration() { }

    static boolean containsBlockedLanguage(String... fields) {
        for (String field : fields) {
            if (field == null || field.isEmpty()) continue;
            List<String> tokens = tokens(field);
            for (String token : tokens) {
                if (isBlockedToken(token)) return true;
            }
            // Catch a term typed as separated single letters, such as "s l u r".
            StringBuilder spacedWord = new StringBuilder();
            for (String token : tokens) {
                if (token.length() == 1) {
                    spacedWord.append(token);
                } else {
                    if (isBlockedToken(spacedWord.toString())) return true;
                    spacedWord.setLength(0);
                }
            }
            if (isBlockedToken(spacedWord.toString())) return true;
        }
        return false;
    }

    private static boolean isBlockedToken(String token) {
        if (BLOCKED_TERMS.contains(token)) return true;
        String normalized = reduceLongRepeats(token);
        if (BLOCKED_TERMS.contains(normalized)) return true;
        String[] suffixes = {"ing", "ers", "er", "ed", "es", "s", "y", "ish"};
        for (String suffix : suffixes) {
            if (normalized.length() > suffix.length() + 2 && normalized.endsWith(suffix)
                    && BLOCKED_TERMS.contains(normalized.substring(0, normalized.length() - suffix.length()))) return true;
        }
        return false;
    }

    private static String reduceLongRepeats(String token) {
        StringBuilder reduced = new StringBuilder(token.length());
        int previous = -1;
        int repeated = 0;
        for (int i = 0; i < token.length(); i++) {
            char current = token.charAt(i);
            if (current == previous) repeated++;
            else repeated = 1;
            if (repeated <= 2) reduced.append(current);
            previous = current;
        }
        return reduced.toString();
    }

    private static List<String> tokens(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFKD)
                .toLowerCase(Locale.ROOT).replaceAll("\\p{M}+", "");
        List<String> result = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        for (int offset = 0; offset < normalized.length();) {
            int point = normalized.codePointAt(offset);
            offset += Character.charCount(point);
            int mapped = mapLookalike(point);
            if (Character.isLetterOrDigit(mapped)) {
                token.appendCodePoint(mapped);
            } else if (Character.isWhitespace(mapped)) {
                addToken(result, token);
            }
            // Punctuation is removed inside a word, so forms split by punctuation are checked too.
        }
        addToken(result, token);
        return result;
    }

    private static void addToken(List<String> result, StringBuilder token) {
        if (token.length() > 0) {
            result.add(token.toString());
            token.setLength(0);
        }
    }

    private static int mapLookalike(int point) {
        switch (point) {
            case '0': case 0x043e: case 0x03bf: return 'o'; // zero, Cyrillic/Greek o
            case '1': case '!': case '|': case 0x0456: case 0x03b9: return 'i';
            case '3': case 0x0435: case 0x03b5: return 'e';
            case '4': case '@': case 0x0430: case 0x03b1: return 'a';
            case '5': case '$': case 0x0455: case 0x03c2: case 0x03c3: return 's';
            case '7': case '+': case 0x0442: case 0x03c4: return 't';
            case '8': case 0x0432: return 'b';
            case 0x0440: case 0x03c1: return 'p';
            case 0x0441: return 'c';
            case 0x0445: case 0x03c7: return 'x';
            case 0x0443: case 0x03c5: return 'y';
            default: return point;
        }
    }
}
