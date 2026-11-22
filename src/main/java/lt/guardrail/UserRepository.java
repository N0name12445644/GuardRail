package lt.guardrail;

import java.util.Map;
import java.util.HashMap;

public class UserRepository {

    private final Map<Integer, User> users = new HashMap<>();

    public void addUser(User user) {
        if (users.containsKey(user.getId())) {
            throw new IllegalArgumentException("User already exists");
        }

        users.put(user.getId(), user);
    }
    public User findUser(int tenantId, int userId) {

        User user = users.get(userId);

        if (user == null || user.getTenantId() != tenantId) {
            throw new IllegalArgumentException("User not found in tenant");
        }

        return user;
    }

}