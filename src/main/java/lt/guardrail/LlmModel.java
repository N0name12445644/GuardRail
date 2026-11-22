package lt.guardrail;

public class LlmModel {

    private final String id;
    private final int cost;

    public LlmModel(String id, int cost) {
        if (cost <= 0) {
            throw new IllegalArgumentException("Cost must be positive");
        }

        this.id = id;
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public int getCost() {
        return cost;
    }
}