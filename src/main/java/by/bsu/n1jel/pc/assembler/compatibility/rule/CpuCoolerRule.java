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
import static by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode.CPU;
import static by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode.CPU_COOLER;

@Component
public class CpuCoolerRule implements CompatibilityRule {

    static final String RULE = "cpu-cooler";

    @Override
    public void check(CompatibilityContext context) {
        Optional<BuildPart> cpu = context.part(CPU);
        Optional<BuildPart> cooler = context.part(CPU_COOLER);
        if (cpu.isEmpty() || cooler.isEmpty()) {
            return;
        }

        Optional<String> socket = context.requireText(RULE, cpu.get(), "socket");
        Optional<List<String>> coolerSockets = context.requireList(RULE, cooler.get(), "cooler_sockets");
        if (socket.isPresent() && coolerSockets.isPresent() && !containsIgnoreCase(coolerSockets.get(), socket.get())) {
            context.error(RULE, "Кулер «%s» не крепится на сокет %s (поддерживает: %s)"
                    .formatted(cooler.get().name(), socket.get(), String.join(", ", coolerSockets.get())),
                    cpu.get(), cooler.get());
        }

        Optional<BigDecimal> cpuTdp = context.requireNumber(RULE, cpu.get(), "tdp_w");
        Optional<BigDecimal> coolerTdp = context.requireNumber(RULE, cooler.get(), "cooler_max_tdp_w");
        if (cpuTdp.isPresent() && coolerTdp.isPresent() && coolerTdp.get().compareTo(cpuTdp.get()) < 0) {
            context.warning(RULE, "Кулер рассчитан на %s Вт, а процессор выделяет %s Вт — возможен перегрев и сброс частот"
                    .formatted(format(coolerTdp.get()), format(cpuTdp.get())), cpu.get(), cooler.get());
        }
    }
}
