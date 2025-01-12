package com.thacbao.codeSphere.utils;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtil {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");

    public static String generateSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        String text = input.toLowerCase(Locale.ENGLISH);

        text = Normalizer.normalize(text, Form.NFD);
        text = NONLATIN.matcher(text).replaceAll("");

        text = WHITESPACE.matcher(text).replaceAll("-");

        text = text.replaceAll("--+", "-");

        text = EDGESDHASHES.matcher(text).replaceAll("");

        return text;
    }

    public static String generateUniqueSlug(String input) {
        String slug = generateSlug(input);
        return slug + "-" + System.currentTimeMillis();
    }
}
