package by.bsu.n1jel.pc.assembler.service.api;

import by.bsu.n1jel.pc.assembler.dto.response.CompatibilityIssueDto;
import by.bsu.n1jel.pc.assembler.dto.response.CompatibilityReportDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CompatibilityService {

    CompatibilityReportDto checkBuild(Long buildId);

    /**
     * Для выбора компонента в слот: какие ошибки появятся, если поставить компонент вместо текущего того же типа.
     * Возвращает id компонента → ошибки (пустой список — совместим).
     */
    Map<Long, List<CompatibilityIssueDto>> checkCandidates(Long buildId, Collection<Long> componentIds);

    /**
     * Проблемы сгруппированные по компонентам — чтобы подсветить слоты.
     */
    static Map<Long, List<CompatibilityIssueDto>> issuesByComponent(CompatibilityReportDto report) {
        Map<Long, List<CompatibilityIssueDto>> result = new HashMap<>();
        for (CompatibilityIssueDto issue : report.issues()) {
            for (Long componentId : issue.componentIds()) {
                result.computeIfAbsent(componentId, id -> new ArrayList<>()).add(issue);
            }
        }
        return result;
    }
}
