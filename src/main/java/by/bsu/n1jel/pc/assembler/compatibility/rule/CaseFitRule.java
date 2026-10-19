package by.bsu.n1jel.pc.assembler.compatibility.rule;

import by.bsu.n1jel.pc.assembler.compatibility.BuildPart;
import by.bsu.n1jel.pc.assembler.compatibility.CompatibilityContext;
import by.bsu.n1jel.pc.assembler.compatibility.CompatibilityRule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static by.bsu.n1jel.pc.assembler.compatibility.CompatibilityContext.containsIgnoreCase;
import static by.bsu.n1jel.pc.assembler.compatibility.CompatibilityContext.format;
import static by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode.*;


@Component
public class CaseFitRule implements CompatibilityRule {

    static final String RULE = "case";

    @Override
    public void check(CompatibilityContext context) {
        Optional<BuildPart> pcCase = context.part(CASE);
        if (pcCase.isEmpty()) {
            return;
        }

        context.part(MOTHERBOARD).ifPresent(board ->
                checkFormFactor(context, pcCase.get(), board, "case_mb_form_factors", "Материнская плата"));
        context.part(PSU).ifPresent(psu ->
                checkFormFactor(context, pcCase.get(), psu, "case_psu_form_factors", "Блок питания"));

        for (BuildPart gpu : context.parts(GPU)) {
            checkSize(context, pcCase.get(), gpu, "length_mm", "case_max_gpu_length_mm", "Видеокарта длиной");
        }

        context.part(CPU_COOLER).ifPresent(cooler -> {
            if (cooler.has("radiator_mm")) {
                checkSize(context, pcCase.get(), cooler, "radiator_mm", "case_max_radiator_mm", "Радиатор СЖО");
            } else if (cooler.has("height_mm")) {
                checkSize(context, pcCase.get(), cooler, "height_mm", "case_max_cooler_height_mm", "Кулер высотой");
            } else {
                context.warning(RULE, "Не удалось проверить, помещается ли кулер «%s» в корпус: не указаны высота или размер радиатора"
                        .formatted(cooler.name()), cooler);
            }
        });
    }

    private void checkFormFactor(CompatibilityContext context, BuildPart pcCase, BuildPart part,
                                 String caseSpec, String what) {
        Optional<String> formFactor = context.requireText(RULE, part, "form_factor");
        Optional<List<String>> supported = context.requireList(RULE, pcCase, caseSpec);
        if (formFactor.isPresent() && supported.isPresent() && !containsIgnoreCase(supported.get(), formFactor.get())) {
            context.error(RULE, "%s формата %s не помещается в корпус (поддерживает: %s)"
                    .formatted(what, formFactor.get(), String.join(", ", supported.get())), part, pcCase);
        }
    }

    private void checkSize(CompatibilityContext context, BuildPart pcCase, BuildPart part,
                           String partSpec, String caseSpec, String what) {
        Optional<BigDecimal> size = context.requireNumber(RULE, part, partSpec);
        Optional<BigDecimal> max = context.requireNumber(RULE, pcCase, caseSpec);
        if (size.isPresent() && max.isPresent() && size.get().compareTo(max.get()) > 0) {
            context.error(RULE, "%s %s мм не помещается в корпус: максимум %s мм"
                    .formatted(what, format(size.get()), format(max.get())), part, pcCase);
        }
    }
}
