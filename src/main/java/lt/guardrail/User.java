package lt.guardrail;

public class User {

    private final int id;
    private final int tenantId;
    private final String name;
    private int credits;

    public User(int id, int tenantId, String name, int credits) {

        if (credits < 0) {
            throw new IllegalArgumentException("Credits cannot be negative");
        }

        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.credits = credits;
    }

    public int getId() {
        return id;
    }

    public int getTenantId() {
        return tenantId;
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    public void deductCredits(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (credits < amount) {
            throw new IllegalStateException("Not enough credits");
        }

        credits -= amount;
    }

}