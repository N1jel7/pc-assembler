package by.bsu.n1jel.pc.assembler.compatibility.rule;

import by.bsu.n1jel.pc.assembler.compatibility.BuildPart;
import by.bsu.n1jel.pc.assembler.compatibility.CompatibilityContext;
import by.bsu.n1jel.pc.assembler.compatibility.CompatibilityRule;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static by.bsu.n1jel.pc.assembler.compatibility.CompatibilityContext.plural;
import static by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode.*;

@Component
public class StoragePortsRule implements CompatibilityRule {

    static final String RULE = "storage";

    @Override
    public void check(CompatibilityContext context) {
        Optional<BuildPart> motherboard = context.part(MOTHERBOARD);
        List<BuildPart> drives = context.parts(STORAGE, SSD, HDD);
        if (motherboard.isEmpty() || drives.isEmpty()) {
            return;
        }

        List<BuildPart> m2Drives = new ArrayList<>();
        List<BuildPart> sataDrives = new ArrayList<>();
        for (BuildPart drive : drives) {
            Optional<String> driveInterface = context.requireText(RULE, drive, "storage_interface");
            if (driveInterface.isEmpty()) {
                continue;
            }
            String value = driveInterface.get().toUpperCase(Locale.ROOT);
            if (value.startsWith("M.2")) {
                m2Drives.add(drive);
            } else if (value.startsWith("SATA")) {
                sataDrives.add(drive);
            }
        }

        checkPorts(context, motherboard.get(), m2Drives, "m2_slots", "M.2", "слот M.2", "слота M.2", "слотов M.2");
        checkPorts(context, motherboard.get(), sataDrives, "sata_ports", "SATA", "порт SATA", "порта SATA", "портов SATA");
    }

    private void checkPorts(CompatibilityContext context, BuildPart motherboard, List<BuildPart> drives,
                            String spec, String kind, String one, String few, String many) {
        int needed = drives.stream().mapToInt(BuildPart::quantity).sum();
        if (needed == 0) {
            return;
        }
        context.requireNumber(RULE, motherboard, spec)
                .filter(available -> needed > available.intValue())
                .ifPresent(available -> {
                    List<BuildPart> related = new ArrayList<>(drives);
                    related.addFirst(motherboard);
                    context.error(RULE, "Накопителей %s: %d, а на материнской плате %s"
                            .formatted(kind, needed, plural(available.intValue(), one, few, many)), related);
                });
    }
}
