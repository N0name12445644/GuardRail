package lt.guardrail;

public class GuardrailService {

    private final CreditService creditService;
    private final PromptInjectionFilter filter;
    private final MaskingService maskingService;
    private final LlmClient llmClient;

    public GuardrailService(
            CreditService creditService,
            PromptInjectionFilter filter,
            MaskingService maskingService,
            LlmClient llmClient
    ) {
        this.creditService = creditService;
        this.filter = filter;
        this.maskingService = maskingService;
        this.llmClient = llmClient;
    }

    public String processRequest(
            int tenantId,
            int userId,
            LlmModel model,
            String text
    ) {

        creditService.checkCredits(tenantId, userId, model.getCost());

        if (filter.isBlocked(text)) {
            throw new IllegalArgumentException("Prompt injection detected");
        }

        MaskingResult result = maskingService.mask(text);

        String response = llmClient.generate(
                model.getId(),
                result.getMaskedText()
        );

        if (response == null || response.isBlank()) {
            throw new IllegalStateException("Invalid LLM response");
        }

        String restoredResponse = maskingService.unmask(
                response,
                result.getMapping()
        );

        creditService.deductCredits(tenantId, userId, model.getCost());

        return restoredResponse;
    }
}