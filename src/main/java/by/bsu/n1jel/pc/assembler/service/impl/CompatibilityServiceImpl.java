package by.bsu.n1jel.pc.assembler.service.impl;

import by.bsu.n1jel.pc.assembler.compatibility.BuildPart;
import by.bsu.n1jel.pc.assembler.compatibility.CompatibilityContext;
import by.bsu.n1jel.pc.assembler.compatibility.CompatibilityRule;
import by.bsu.n1jel.pc.assembler.compatibility.CompatibilitySeverity;
import by.bsu.n1jel.pc.assembler.dto.response.CompatibilityIssueDto;
import by.bsu.n1jel.pc.assembler.dto.response.CompatibilityReportDto;
import by.bsu.n1jel.pc.assembler.entity.Build;
import by.bsu.n1jel.pc.assembler.entity.Component;
import by.bsu.n1jel.pc.assembler.entity.ComponentType;
import by.bsu.n1jel.pc.assembler.entity.Specification;
import by.bsu.n1jel.pc.assembler.entity.SpecificationType;
import by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode;
import by.bsu.n1jel.pc.assembler.repository.BuildRepository;
import by.bsu.n1jel.pc.assembler.repository.ComponentRepository;
import by.bsu.n1jel.pc.assembler.repository.SpecificationTypeRepository;
import by.bsu.n1jel.pc.assembler.service.api.CompatibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static by.bsu.n1jel.pc.assembler.exception.common.ResourceExceptionFactory.buildNotFoundException;

@Service
@RequiredArgsConstructor
public class CompatibilityServiceImpl implements CompatibilityService {

    private final BuildRepository buildRepository;
    private final ComponentRepository componentRepository;
    private final SpecificationTypeRepository specificationTypeRepository;
    private final List<CompatibilityRule> rules;

    @Override
    @Transactional(readOnly = true)
    public CompatibilityReportDto checkBuild(Long buildId) {
        CompatibilityContext context = run(buildParts(buildId), specNames());

        List<CompatibilityIssueDto> issues = context.issues().stream()
                .sorted(Comparator.comparing(CompatibilityIssueDto::severity))
                .toList();
        long errors = count(issues, CompatibilitySeverity.ERROR);

        return new CompatibilityReportDto(
                buildId,
                errors == 0,
                count(issues, CompatibilitySeverity.INCOMPLETE) == 0,
                errors,
                count(issues, CompatibilitySeverity.WARNING),
                issues);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<CompatibilityIssueDto>> checkCandidates(Long buildId, Collection<Long> componentIds) {
        List<BuildPart> buildParts = buildParts(buildId);
        Map<String, String> specNames = specNames();

        Map<Long, List<CompatibilityIssueDto>> result = new HashMap<>();
        for (Component candidate : componentRepository.findAllById(componentIds)) {
            ComponentTypeCode typeCode = candidate.getComponentType().getCode();
            if (typeCode == null) {
                result.put(candidate.getId(), List.of());
                continue;
            }

            // выбор в слот заменяет компонент того же типа
            List<BuildPart> parts = new ArrayList<>(buildParts.stream()
                    .filter(part -> part.typeCode() != typeCode)
                    .toList());
            parts.add(toPart(candidate, 1));

            result.put(candidate.getId(), run(parts, specNames).issues().stream()
                    .filter(issue -> issue.severity() == CompatibilitySeverity.ERROR)
                    .filter(issue -> issue.componentIds().contains(candidate.getId()))
                    .toList());
        }
        return result;
    }

    private List<BuildPart> buildParts(Long buildId) {
        Build build = buildRepository.findById(buildId)
                .orElseThrow(() -> buildNotFoundException(buildId));

        return build.getBuildPartitions() == null
                ? List.of()
                : build.getBuildPartitions().stream()
                        .filter(partition -> partition.getComponent().getComponentType().getCode() != null)
                        .map(partition -> toPart(partition.getComponent(), partition.getQuantity()))
                        .toList();
    }

    private CompatibilityContext run(List<BuildPart> parts, Map<String, String> specNames) {
        CompatibilityContext context = new CompatibilityContext(parts, specNames);
        rules.forEach(rule -> rule.check(context));
        return context;
    }

    private Map<String, String> specNames() {
        return specificationTypeRepository.findAll().stream()
                .filter(type -> type.getCode() != null)
                .collect(Collectors.toMap(SpecificationType::getCode, SpecificationType::getName));
    }

    private static BuildPart toPart(Component component, int quantity) {
        ComponentType type = component.getComponentType();

        Map<String, String> specs = new HashMap<>();
        for (Specification spec : component.getSpecifications()) {
            if (spec.getType().getCode() != null) {
                specs.put(spec.getType().getCode(), spec.getValue());
            }
        }

        return new BuildPart(
                component.getId(),
                component.getName(),
                type.getCode(),
                quantity,
                component.getStockQuantity(),
                type.getMinAmount(),
                type.getMaxAmount(),
                specs);
    }

    private static long count(List<CompatibilityIssueDto> issues, CompatibilitySeverity severity) {
        return issues.stream().filter(issue -> issue.severity() == severity).count();
    }
}
