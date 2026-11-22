package lt.guardrail;

import java.util.List;
import java.util.Locale;

public class PromptInjectionFilter {

    private static final List<String> BLOCKED_PHRASES = List.of(
            "ignore previous instructions",
            "ignore previous rules",
            "ignore all instructions",
            "reveal system prompt",
            "print secrets"
    );

    public boolean isBlocked(String text) {

        String normalizedText = text.toLowerCase(Locale.ROOT);

        for (String phrase : BLOCKED_PHRASES) {
            if (normalizedText.contains(phrase)) {
                return true;
            }
        }

        return false;
    }
}