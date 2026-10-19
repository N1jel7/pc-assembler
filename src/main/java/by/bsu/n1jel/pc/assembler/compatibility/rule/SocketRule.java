package by.bsu.n1jel.pc.assembler.compatibility.rule;

import by.bsu.n1jel.pc.assembler.compatibility.BuildPart;
import by.bsu.n1jel.pc.assembler.compatibility.CompatibilityContext;
import by.bsu.n1jel.pc.assembler.compatibility.CompatibilityRule;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode.CPU;
import static by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode.MOTHERBOARD;

@Component
public class SocketRule implements CompatibilityRule {

    static final String RULE = "socket";

    @Override
    public void check(CompatibilityContext context) {
        Optional<BuildPart> cpu = context.part(CPU);
        Optional<BuildPart> motherboard = context.part(MOTHERBOARD);
        if (cpu.isEmpty() || motherboard.isEmpty()) {
            return;
        }

        Optional<String> cpuSocket = context.requireText(RULE, cpu.get(), "socket");
        Optional<String> boardSocket = context.requireText(RULE, motherboard.get(), "socket");
        if (cpuSocket.isPresent() && boardSocket.isPresent() && !cpuSocket.get().equalsIgnoreCase(boardSocket.get())) {
            context.error(RULE, "Процессор с сокетом %s не встанет в материнскую плату с сокетом %s"
                    .formatted(cpuSocket.get(), boardSocket.get()), cpu.get(), motherboard.get());
        }
    }
}
