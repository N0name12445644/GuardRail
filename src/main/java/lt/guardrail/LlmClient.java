package lt.guardrail;

public interface LlmClient {

    String generate(String modelId, String text);

}