# LLM Guardrail

A Java prototype for secure processing of requests to Large Language Models (LLMs).

The application checks user credits, detects basic prompt injection attempts, masks sensitive information before sending requests to an LLM, and restores the original information in the generated response.

The project supports multiple tenants and includes both a mock LLM client and a real Google Gemini API integration.

## Technologies

- Java 21
- Apache Maven
- JUnit 5
- Google Gson 2.13.2
- Java HTTP Client
- Google Gemini API
- IntelliJ IDEA

## Main Features

### 1. Credit Management

Each user has a credit balance. Before processing a request, the application verifies that the user has enough credits for the selected model.

Credits are deducted only after successful request processing. Rejected requests and failed LLM responses do not deduct credits.

### 2. Prompt Injection Detection

The `PromptInjectionFilter` detects selected suspicious phrases, such as:

- `ignore previous instructions`
- `ignore previous rules`
- `reveal system prompt`
- `print secrets`

Requests containing these phrases are rejected.

This is a basic rule-based filter, not a complete prompt injection protection system.

### 3. Sensitive Data Masking

The `MaskingService` uses regular expressions to identify and replace certain types of sensitive information:

- Email addresses
- Lithuanian phone numbers
- Lithuanian personal identification codes
- Payment card numbers
- Selected API key formats

Example:

**Original request:**

`Write an email to jonas@gmail.com`

**Masked request sent to the LLM:**

`Write an email to [EMAIL_1]`

The original values are stored in a temporary mapping associated with the request.

When the LLM returns a response containing the token, the system restores the original value.

Masking is not encryption and does not guarantee detection of all sensitive information.

### 4. Multi-Tenant Isolation

The application supports multiple organizations (tenants).

Each user belongs to a specific tenant. The `UserRepository` validates the tenant ID before returning a user.

Credit operations use the same validation, preventing access to and modification of another tenant's user balance through the service methods.

The prototype does not yet implement authentication or a database.

### 5. LLM Integration

The application includes two implementations of the `LlmClient` interface:

- `MockLlmClient` — provides predictable responses without an internet connection or API key.
- `GeminiClient` — sends HTTP requests to the Google Gemini API and processes generated responses.

The current `Main.java` uses `MockLlmClient` for reliable demonstration.

The Gemini integration is implemented but depends on external service availability.

## Project Structure

```text
src/
├── main/java/lt/guardrail/
│   ├── Main.java
│   ├── Tenant.java
│   ├── User.java
│   ├── UserRepository.java
│   ├── LlmModel.java
│   ├── CreditService.java
│   ├── PromptInjectionFilter.java
│   ├── MaskingService.java
│   ├── MaskingResult.java
│   ├── LlmClient.java
│   ├── MockLlmClient.java
│   ├── GeminiClient.java
│   └── GuardrailService.java
└── test/java/lt/guardrail/
    └── GuardrailServiceTest.java
```

## Requirements

- JDK 21
- Apache Maven 3.x
- Internet access and a Gemini API key only when using `GeminiClient`

## Build and Run

Open the project in IntelliJ IDEA and load the Maven dependencies.

To run the application from IntelliJ IDEA:

1. Open `src/main/java/lt/guardrail/Main.java`.
2. Run the `main()` method.
3. View the demonstration results in the Run console.

Alternatively, from the project root, use Maven to compile the project:

```bash
mvn compile
```

Then run the demonstration:

```bash
java -cp target/classes lt.guardrail.Main
```

The default demonstration uses Mock mode and does not require an API key or internet access.

On Windows, Maven commands can also be executed using IntelliJ IDEA's Maven tool window if Maven is not installed globally.

## Gemini API Configuration

To use the real Gemini integration:

1. Obtain an API key from [Google AI Studio](https://aistudio.google.com/apikey).
2. Set the `GEMINI_API_KEY` environment variable.
3. Configure `Main.java` to create a `GeminiClient` instead of a `MockLlmClient`.
4. Use an appropriate Gemini model ID in `LlmModel`.
5. Run the application.

Example Java configuration:

```java
String apiKey = System.getenv("GEMINI_API_KEY");
LlmClient client = new GeminiClient(apiKey);
LlmModel model = new LlmModel("gemini-3.1-flash-lite", 5);
```

Never commit API keys to the repository.

The credit cost is an internal prototype value and does not represent the provider's actual API pricing.

## Automated Tests

The project includes six automated tests implemented using JUnit 5.

| Test | Description |
|---|---|
| `successfulRequest()` | Verifies successful processing and credit deduction |
| `promptInjectionBlocked()` | Verifies rejection of a suspicious request without deducting credits |
| `insufficientCredits()` | Verifies rejection when the user has insufficient credits |
| `tenantIsolation()` | Verifies that cross-tenant reads and credit modifications are blocked |
| `llmReceivesMaskedText()` | Verifies that the LLM receives masked data and original values are restored |
| `invalidLlmResponse()` | Verifies that an empty LLM response causes an error without deducting credits |

Run tests using:

```bash
mvn test
```

The tests use mock implementations and do not require a Gemini API key.

## Demonstration Scenarios

The application initializes two tenants and three users.

| Tenant | User | Initial Credits |
|---|---|---:|
| Company A | Jonas | 100 |
| Company A | Peter | 50 |
| Company B | Mark | 2 |

The `Main` class demonstrates four scenarios:

1. **Successful request:** Jonas sends a request containing an email address. The application masks the email, processes the request, restores the address, and deducts 5 credits.
2. **Prompt injection:** A request containing a blocked phrase is rejected, without deducting credits.
3. **Insufficient credits:** Mark attempts a request costing 5 credits while having only 2. The request is rejected.
4. **Tenant isolation:** Company A attempts to access Mark, who belongs to Company B. The request is rejected.

### Expected Console Output

```text
Mode: Mock
Credits before: 100
AI response: Write an email to jonas@gmail.com
Credits after: 95
Error: Prompt injection detected
Credits: 95
Error: Not enough credits
Error: User not found in tenant
```

## Current Limitations

This project is a coursework prototype rather than a production-ready security service.

Current limitations include:

- Users and balances are stored in memory.
- Tenant identifiers are supplied by trusted demonstration code; authentication is not implemented.
- Prompt injection detection is based on a limited list of phrases.
- Sensitive data detection relies on regular expressions and may miss some formats.
- Token restoration depends on the LLM preserving the placeholders.
- Credit operations are designed for a simple single-process demonstration rather than concurrent production requests.
- The Gemini integration depends on external API availability and does not currently implement automatic retries.

Potential future improvements include persistent storage, authentication, REST API endpoints, more advanced validation, and improved error handling.
