package by.bsu.n1jel.pc.assembler.compatibility.rule;

import by.bsu.n1jel.pc.assembler.compatibility.BuildPart;
import by.bsu.n1jel.pc.assembler.compatibility.CompatibilityContext;
import by.bsu.n1jel.pc.assembler.compatibility.CompatibilityRule;
import by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode.*;

@Component
public class CompletenessRule implements CompatibilityRule {

    static final String RULE = "completeness";

    private static final Map<ComponentTypeCode, String> REQUIRED = new LinkedHashMap<>();

    static {
        REQUIRED.put(CPU, "процессор");
        REQUIRED.put(MOTHERBOARD, "материнская плата");
        REQUIRED.put(RAM, "оперативная память");
        REQUIRED.put(PSU, "блок питания");
        REQUIRED.put(CASE, "корпус");
    }

    @Override
    public void check(CompatibilityContext context) {
        REQUIRED.forEach((code, name) -> {
            if (context.part(code).isEmpty()) {
                context.incomplete(RULE, "Не выбран обязательный компонент: " + name);
            }
        });

        if (context.parts(STORAGE, SSD, HDD).isEmpty()) {
            context.incomplete(RULE, "Не выбран накопитель: системе негде храниться");
        }

        Optional<BuildPart> cpu = context.part(CPU);
        if (cpu.isPresent() && context.part(GPU).isEmpty()) {
            Optional<Boolean> integratedGraphics = context.requireBool(RULE, cpu.get(), "integrated_graphics");
            if (integratedGraphics.isPresent() && !integratedGraphics.get()) {
                context.incomplete(RULE, "Нужна видеокарта: у процессора нет встроенной графики", cpu.get());
            }
        }

        if (cpu.isPresent() && context.part(CPU_COOLER).isEmpty()) {
            context.warning(RULE, "Не выбран кулер процессора — он нужен, если процессор продаётся без боксового кулера",
                    cpu.get());
        }
    }
}
