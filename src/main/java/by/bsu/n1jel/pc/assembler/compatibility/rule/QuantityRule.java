package by.bsu.n1jel.pc.assembler.compatibility.rule;

import by.bsu.n1jel.pc.assembler.compatibility.BuildPart;
import by.bsu.n1jel.pc.assembler.compatibility.CompatibilityContext;
import by.bsu.n1jel.pc.assembler.compatibility.CompatibilityRule;
import org.springframework.stereotype.Component;

@Component
public class QuantityRule implements CompatibilityRule {

    static final String RULE = "quantity";

    @Override
    public void check(CompatibilityContext context) {
        for (BuildPart part : context.parts()) {
            if (part.quantity() < part.minAmount() || part.quantity() > part.maxAmount()) {
                context.error(RULE, "«%s»: в сборке %d шт., а допустимо от %d до %d"
                        .formatted(part.name(), part.quantity(), part.minAmount(), part.maxAmount()), part);
            }

            if (part.stockQuantity() <= 0) {
                context.warning(RULE, "«%s» нет на складе".formatted(part.name()), part);
            } else if (part.stockQuantity() < part.quantity()) {
                context.warning(RULE, "«%s»: в сборке %d шт., а на складе только %d"
                        .formatted(part.name(), part.quantity(), part.stockQuantity()), part);
            }
        }
    }
}
