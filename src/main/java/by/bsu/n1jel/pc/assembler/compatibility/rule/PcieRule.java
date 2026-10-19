package by.bsu.n1jel.pc.assembler.compatibility.rule;

import by.bsu.n1jel.pc.assembler.compatibility.BuildPart;
import by.bsu.n1jel.pc.assembler.compatibility.CompatibilityContext;
import by.bsu.n1jel.pc.assembler.compatibility.CompatibilityRule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

import static by.bsu.n1jel.pc.assembler.compatibility.CompatibilityContext.format;
import static by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode.GPU;
import static by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode.MOTHERBOARD;


@Component
public class PcieRule implements CompatibilityRule {

    static final String RULE = "pcie";

    @Override
    public void check(CompatibilityContext context) {
        Optional<BuildPart> motherboard = context.part(MOTHERBOARD);
        Optional<BigDecimal> boardVersion = motherboard.flatMap(board -> board.number("pcie_version"));
        if (boardVersion.isEmpty()) {
            return;
        }

        for (BuildPart gpu : context.parts(GPU)) {
            gpu.number("pcie_version")
                    .filter(gpuVersion -> gpuVersion.compareTo(boardVersion.get()) > 0)
                    .ifPresent(gpuVersion -> context.warning(RULE,
                            "Видеокарта PCIe %s будет работать в режиме PCIe %s материнской платы — немного медленнее"
                                    .formatted(format(gpuVersion), format(boardVersion.get())), gpu, motherboard.get()));
        }
    }
}
