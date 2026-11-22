package lt.guardrail;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MaskingService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._-]+@[a-zA-Z0-9]+\\.[a-zA-Z]+"
    );
    public MaskingResult mask(String text) {

        Map<String, String> mapping = new HashMap<>();
        Map<String, String> reverseMapping = new HashMap<>();

        int counter = 1;

        Matcher matcher = EMAIL_PATTERN.matcher(text);

        StringBuilder maskedText = new StringBuilder();

        while (matcher.find()) {
            String email = matcher.group();
            String token;

            if (reverseMapping.containsKey(email)) {
                token = reverseMapping.get(email);
            } else {
                token = "[EMAIL_" + counter + "]";
                mapping.put(token, email);
                reverseMapping.put(email, token);
                counter++;
            }
            matcher.appendReplacement(maskedText, token);
        }
        matcher.appendTail(maskedText);
        return new MaskingResult(maskedText.toString(), mapping);
    }

    public String unmask(String text, Map<String, String> mapping) {

        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }

        return text;
    }

}