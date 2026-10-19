package by.bsu.n1jel.pc.assembler.dto.response;

import java.util.List;

public record CompatibilityReportDto(
        Long buildId,
        boolean compatible,
        boolean complete,
        long errors,
        long warnings,
        List<CompatibilityIssueDto> issues
) {
}
