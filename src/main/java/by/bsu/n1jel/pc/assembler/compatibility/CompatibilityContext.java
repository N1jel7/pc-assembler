package by.bsu.n1jel.pc.assembler.compatibility;

import by.bsu.n1jel.pc.assembler.dto.response.CompatibilityIssueDto;
import by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiFunction;


public class CompatibilityContext {

    private final List<BuildPart> parts;
    private final Map<String, String> specNames;
    private final List<CompatibilityIssueDto> issues = new ArrayList<>();
    private final Set<String> reportedMissing = new HashSet<>();

    public CompatibilityContext(List<BuildPart> parts, Map<String, String> specNames) {
        this.parts = List.copyOf(parts);
        this.specNames = specNames;
    }

    public List<BuildPart> parts() {
        return parts;
    }

    public Optional<BuildPart> part(ComponentTypeCode typeCode) {
        return parts.stream().filter(part -> part.typeCode() == typeCode).findFirst();
    }

    public List<BuildPart> parts(ComponentTypeCode... typeCodes) {
        Set<ComponentTypeCode> codes = Set.of(typeCodes);
        return parts.stream().filter(part -> codes.contains(part.typeCode())).toList();
    }

    public List<CompatibilityIssueDto> issues() {
        return List.copyOf(issues);
    }

    public void error(String rule, String message, BuildPart... related) {
        add(CompatibilitySeverity.ERROR, rule, message, related);
    }

    public void error(String rule, String message, List<BuildPart> related) {
        error(rule, message, related.toArray(BuildPart[]::new));
    }

    public void warning(String rule, String message, List<BuildPart> related) {
        warning(rule, message, related.toArray(BuildPart[]::new));
    }

    public void warning(String rule, String message, BuildPart... related) {
        add(CompatibilitySeverity.WARNING, rule, message, related);
    }

    public void incomplete(String rule, String message, BuildPart... related) {
        add(CompatibilitySeverity.INCOMPLETE, rule, message, related);
    }

    public Optional<String> requireText(String rule, BuildPart part, String code) {
        return require(rule, part, code, BuildPart::text);
    }

    public Optional<BigDecimal> requireNumber(String rule, BuildPart part, String code) {
        return require(rule, part, code, BuildPart::number);
    }

    public Optional<Boolean> requireBool(String rule, BuildPart part, String code) {
        return require(rule, part, code, BuildPart::bool);
    }

    public Optional<List<String>> requireList(String rule, BuildPart part, String code) {
        return require(rule, part, code, BuildPart::list);
    }

    private <T> Optional<T> require(String rule, BuildPart part, String code,
                                    BiFunction<BuildPart, String, Optional<T>> getter) {
        Optional<T> value = getter.apply(part, code);
        if (value.isEmpty() && reportedMissing.add(part.componentId() + ":" + code)) {
            warning(rule, "Не удалось проверить совместимость: у «%s» не указано «%s»"
                    .formatted(part.name(), specNames.getOrDefault(code, code)), part);
        }
        return value;
    }

    private void add(CompatibilitySeverity severity, String rule, String message, BuildPart... related) {
        issues.add(new CompatibilityIssueDto(severity, rule, message,
                Arrays.stream(related).map(BuildPart::componentId).distinct().toList()));
    }

    public static String format(BigDecimal number) {
        return number.stripTrailingZeros().toPlainString();
    }


    public static String plural(long n, String one, String few, String many) {
        long mod10 = n % 10;
        long mod100 = n % 100;
        String word = mod10 == 1 && mod100 != 11 ? one
                : mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14) ? few
                : many;
        return n + " " + word;
    }

    public static boolean containsIgnoreCase(List<String> values, String value) {
        return values.stream().anyMatch(item -> item.equalsIgnoreCase(value));
    }
}
