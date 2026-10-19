package by.bsu.n1jel.pc.assembler.compatibility.rule;

import by.bsu.n1jel.pc.assembler.compatibility.BuildPart;
import by.bsu.n1jel.pc.assembler.compatibility.CompatibilityContext;
import by.bsu.n1jel.pc.assembler.compatibility.CompatibilityRule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static by.bsu.n1jel.pc.assembler.compatibility.CompatibilityContext.format;
import static by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode.*;

@Component
public class PowerRule implements CompatibilityRule {

    static final String RULE = "power";

    /**
     * Плата, память, накопители, вентиляторы.
     */
    static final BigDecimal REST_OF_SYSTEM_W = BigDecimal.valueOf(100);

    /**
     * Рекомендуемая нагрузка на блок питания — не больше 80%.
     */
    static final BigDecimal RECOMMENDED_LOAD = new BigDecimal("0.8");

    @Override
    public void check(CompatibilityContext context) {
        Optional<BuildPart> psu = context.part(PSU);
        List<BuildPart> consumers = context.parts(CPU, GPU);
        if (psu.isEmpty() || consumers.isEmpty()) {
            return;
        }

        Optional<BigDecimal> wattage = context.requireNumber(RULE, psu.get(), "wattage_w");
        BigDecimal load = REST_OF_SYSTEM_W;
        boolean known = true;
        for (BuildPart consumer : consumers) {
            Optional<BigDecimal> tdp = context.requireNumber(RULE, consumer, "tdp_w");
            if (tdp.isEmpty()) {
                known = false;
            } else {
                load = load.add(tdp.get().multiply(BigDecimal.valueOf(consumer.quantity())));
            }
        }
        if (wattage.isEmpty() || !known) {
            return;
        }

        List<BuildPart> related = new ArrayList<>(consumers);
        related.addFirst(psu.get());
        if (wattage.get().compareTo(load) < 0) {
            context.error(RULE, "Блока питания %s Вт не хватит: сборка потребляет около %s Вт"
                    .formatted(format(wattage.get()), format(load)), related);
        } else if (load.compareTo(wattage.get().multiply(RECOMMENDED_LOAD)) > 0) {
            BigDecimal recommended = load.divide(RECOMMENDED_LOAD, 0, RoundingMode.UP)
                    .divide(BigDecimal.valueOf(50), 0, RoundingMode.UP)
                    .multiply(BigDecimal.valueOf(50));
            context.warning(RULE, "Блок питания %s Вт будет нагружен больше чем на 80%% (около %s Вт) — лучше от %s Вт"
                    .formatted(format(wattage.get()), format(load), format(recommended)), related);
        }
    }
}
