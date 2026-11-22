package lt.guardrail;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MaskingService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._-]+@[a-zA-Z0-9]+\\.[a-zA-Z]+"
    );
    private static final Pattern CARD_PATTERN = Pattern.compile(
            "\\b(?:\\d{4}[ -]?){3}\\d{4}\\b"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(?<!\\d)(?:\\+370|0)[ -]?6\\d{2}[ -]?\\d{5}(?!\\d)"
    );

    private static final Pattern PERSONAL_CODE_PATTERN = Pattern.compile(
            "\\b[1-6]\\d{10}\\b"
    );

    private static final Pattern API_KEY_PATTERN = Pattern.compile(
            "\\b(?:sk-|AIza)[a-zA-Z0-9_-]{20,}\\b"
    );
    public MaskingResult mask(String text) {

        Map<String, String> mapping = new HashMap<>();

        text = maskByPattern(text, EMAIL_PATTERN, "EMAIL", mapping);
        text = maskByPattern(text, API_KEY_PATTERN, "API_KEY", mapping);
        text = maskByPattern(text, CARD_PATTERN, "CARD", mapping);
        text = maskByPattern(text, PHONE_PATTERN, "PHONE", mapping);
        text = maskByPattern(text, PERSONAL_CODE_PATTERN, "PERSONAL_CODE", mapping);

        return new MaskingResult(text, mapping);
    }

    public String unmask(String text, Map<String, String> mapping) {

        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }

        return text;
    }

    private String maskByPattern(
            String text,
            Pattern pattern,
            String tokenType,
            Map<String, String> mapping
    ) {
        Map<String, String> reverseMapping = new HashMap<>();
        int counter = 1;

        Matcher matcher = pattern.matcher(text);
        StringBuilder maskedText = new StringBuilder();

        while (matcher.find()) {
            String originalValue = matcher.group();
            String token;

            if (reverseMapping.containsKey(originalValue)) {
                token = reverseMapping.get(originalValue);
            } else {
                token = "[" + tokenType + "_" + counter + "]";

                mapping.put(token, originalValue);
                reverseMapping.put(originalValue, token);

                counter++;
            }

            matcher.appendReplacement(maskedText, token);
        }

        matcher.appendTail(maskedText);

        return maskedText.toString();
    }

}