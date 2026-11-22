package lt.guardrail;

public class Main {
    public static void main(String[] args) {

        MaskingService service = new MaskingService();

        String text = "Contact jonas@gmail.com or peter@vu.lt or jonas@gmail.com please.";

        MaskingResult result = service.mask(text);

        System.out.println(result.getMaskedText());
        System.out.println(result.getMapping());

        String restoredText = service.unmask(
                result.getMaskedText(),
                result.getMapping()
        );

        System.out.println("Restored: " + restoredText);
    }
}