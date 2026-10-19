package by.bsu.n1jel.pc.assembler.dto.response;

import by.bsu.n1jel.pc.assembler.compatibility.CompatibilitySeverity;

import java.util.List;

public record CompatibilityIssueDto(
        CompatibilitySeverity severity,
        String rule,
        String message,
        List<Long> componentIds
) {
}
