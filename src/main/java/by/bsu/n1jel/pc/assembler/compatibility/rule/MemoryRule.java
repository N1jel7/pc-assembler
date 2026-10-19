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
import static by.bsu.n1jel.pc.assembler.compatibility.CompatibilityContext.plural;
import static by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode.*;


@Component
public class MemoryRule implements CompatibilityRule {

    static final String RULE = "memory";

    @Override
    public void check(CompatibilityContext context) {
        Optional<BuildPart> ram = context.part(RAM);
        if (ram.isEmpty()) {
            return;
        }
        Optional<BuildPart> motherboard = context.part(MOTHERBOARD);
        Optional<BuildPart> cpu = context.part(CPU);

        checkType(context, ram.get(), motherboard, cpu);
        checkModules(context, ram.get(), motherboard);
        checkCapacity(context, ram.get(), motherboard, cpu);
        checkSpeed(context, ram.get(), motherboard, cpu);
    }

    private void checkType(CompatibilityContext context, BuildPart ram,
                           Optional<BuildPart> motherboard, Optional<BuildPart> cpu) {
        Optional<String> ramType = context.requireText(RULE, ram, "memory_type");
        if (ramType.isEmpty()) {
            return;
        }

        motherboard.flatMap(board -> context.requireText(RULE, board, "memory_type"))
                .filter(boardType -> !boardType.equalsIgnoreCase(ramType.get()))
                .ifPresent(boardType -> context.error(RULE,
                        "Память %s не подходит к материнской плате, которая поддерживает только %s"
                                .formatted(ramType.get(), boardType), ram, motherboard.get()));

        cpu.flatMap(processor -> context.requireList(RULE, processor, "supported_memory_types"))
                .filter(supported -> !containsIgnoreCase(supported, ramType.get()))
                .ifPresent(supported -> context.error(RULE,
                        "Процессор не работает с памятью %s (поддерживает: %s)"
                                .formatted(ramType.get(), String.join(", ", supported)), ram, cpu.get()));
    }

    private void checkModules(CompatibilityContext context, BuildPart ram, Optional<BuildPart> motherboard) {
        Optional<BigDecimal> perKit = context.requireNumber(RULE, ram, "memory_modules");
        if (perKit.isEmpty()) {
            return;
        }
        int modules = perKit.get().intValue() * ram.quantity();

        motherboard.flatMap(board -> context.requireNumber(RULE, board, "ram_slots"))
                .filter(slots -> modules > slots.intValue())
                .ifPresent(slots -> context.error(RULE,
                        "%s памяти не поместятся: у материнской платы %s"
                                .formatted(plural(modules, "планка", "планки", "планок"),
                                        plural(slots.intValue(), "слот", "слота", "слотов")),
                        ram, motherboard.get()));

        if (modules == 1) {
            context.warning(RULE, "Одна планка памяти работает в одноканальном режиме — лучше две", ram);
        } else if (modules % 2 != 0) {
            context.warning(RULE, "Нечётное число планок (%d): часть памяти будет работать без двухканального режима"
                    .formatted(modules), ram);
        }
    }

    private void checkCapacity(CompatibilityContext context, BuildPart ram,
                               Optional<BuildPart> motherboard, Optional<BuildPart> cpu) {
        Optional<BigDecimal> perKit = context.requireNumber(RULE, ram, "capacity_gb");
        if (perKit.isEmpty()) {
            return;
        }
        BigDecimal total = perKit.get().multiply(BigDecimal.valueOf(ram.quantity()));

        for (BuildPart limiter : List.of(motherboard, cpu).stream().flatMap(Optional::stream).toList()) {
            context.requireNumber(RULE, limiter, "max_memory_gb")
                    .filter(max -> total.compareTo(max) > 0)
                    .ifPresent(max -> context.error(RULE, "Всего %s ГБ памяти, а «%s» поддерживает максимум %s ГБ"
                            .formatted(format(total), limiter.name(), format(max)), ram, limiter));
        }
    }

    private void checkSpeed(CompatibilityContext context, BuildPart ram,
                            Optional<BuildPart> motherboard, Optional<BuildPart> cpu) {
        Optional<BigDecimal> speed = context.requireNumber(RULE, ram, "memory_speed_mts");
        if (speed.isEmpty()) {
            return;
        }

        BuildPart slowest = null;
        BigDecimal limit = null;
        for (BuildPart limiter : List.of(motherboard, cpu).stream().flatMap(Optional::stream).toList()) {
            Optional<BigDecimal> max = context.requireNumber(RULE, limiter, "max_memory_speed_mts");
            if (max.isPresent() && (limit == null || max.get().compareTo(limit) < 0)) {
                limit = max.get();
                slowest = limiter;
            }
        }

        if (limit != null && speed.get().compareTo(limit) > 0) {
            context.warning(RULE, "Память %s МТ/с будет работать на %s МТ/с — это максимум для «%s»"
                    .formatted(format(speed.get()), format(limit), slowest.name()), ram, slowest);
        }
    }
}
