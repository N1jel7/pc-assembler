package by.bsu.n1jel.pc.assembler.compatibility;

import by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode;
import by.bsu.n1jel.pc.assembler.service.utils.SpecificationValueUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record BuildPart(
        Long componentId,
        String name,
        ComponentTypeCode typeCode,
        int quantity,
        int stockQuantity,
        int minAmount,
        int maxAmount,
        Map<String, String> specs
) {

    public boolean has(String code) {
        String value = specs.get(code);
        return value != null && !value.isBlank();
    }

    public Optional<String> text(String code) {
        return has(code) ? Optional.of(specs.get(code).trim()) : Optional.empty();
    }

    public Optional<BigDecimal> number(String code) {
        return text(code).flatMap(SpecificationValueUtil::parseNumber);
    }

    public Optional<Boolean> bool(String code) {
        return text(code).flatMap(SpecificationValueUtil::parseBoolean);
    }

    public Optional<List<String>> list(String code) {
        return text(code).map(SpecificationValueUtil::splitList);
    }
}
