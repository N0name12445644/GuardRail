package lt.guardrail;

import java.util.Map;

public class MaskingResult {

    private final String maskedText;
    private final Map<String, String> mapping;

    public MaskingResult(String maskedText, Map<String, String> mapping) {
        this.maskedText = maskedText;
        this.mapping = Map.copyOf(mapping);
    }

    public String getMaskedText() {
        return maskedText;
    }

    public Map<String, String> getMapping() {
        return mapping;
    }

}