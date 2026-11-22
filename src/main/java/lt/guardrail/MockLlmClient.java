package lt.guardrail;

public class MockLlmClient implements LlmClient {

    @Override
    public String generate(String modelId, String text) {
        return "AI response: " + text;
    }
}