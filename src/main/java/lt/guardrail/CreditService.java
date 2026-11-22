package lt.guardrail;

public class CreditService {

    private final UserRepository repository;

    public CreditService(UserRepository repository) {
        this.repository = repository;
    }

    public void checkCredits(int tenantId, int userId, int cost) {
        User user = repository.findUser(tenantId, userId);

        if (user.getCredits() < cost) {
            throw new IllegalStateException("Not enough credits");
        }
    }

    public void deductCredits(int tenantId, int userId, int cost) {
        User user = repository.findUser(tenantId, userId);

        user.deductCredits(cost);
    }
}