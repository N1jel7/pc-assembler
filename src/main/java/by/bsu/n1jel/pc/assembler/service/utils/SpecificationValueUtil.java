package by.bsu.n1jel.pc.assembler.service.utils;

import by.bsu.n1jel.pc.assembler.entity.SpecificationType;
import by.bsu.n1jel.pc.assembler.entity.enums.SpecValueType;
import by.bsu.n1jel.pc.assembler.exception.InvalidRequestException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Значения характеристик хранятся строкой в едином формате, чтобы проверки совместимости могли их сравнивать:
 * NUMBER — "5200" / "5.3", LIST — "DDR4, DDR5", BOOLEAN — "true" / "false".
 */
public class SpecificationValueUtil {

    private static final Set<String> TRUE_VALUES = Set.of("true", "да", "есть", "yes", "1", "+");
    private static final Set<String> FALSE_VALUES = Set.of("false", "нет", "no", "0", "-");

    public static String normalize(SpecificationType type, String rawValue) {
        String value = rawValue == null ? "" : rawValue.trim();
        if (value.isEmpty()) {
            throw new InvalidRequestException("«%s»: значение не заполнено".formatted(type.getName()));
        }

        return switch (type.getValueType()) {
            case STRING -> value;
            case NUMBER -> parseNumber(value)
                    .map(number -> number.stripTrailingZeros().toPlainString())
                    .orElseThrow(() -> new InvalidRequestException(
                            "«%s»: нужно число, получено «%s»".formatted(type.getName(), value)));
            case LIST -> String.join(", ", splitList(value));
            case BOOLEAN -> parseBoolean(value)
                    .map(String::valueOf)
                    .orElseThrow(() -> new InvalidRequestException(
                            "«%s»: нужно «да» или «нет», получено «%s»".formatted(type.getName(), value)));
        };
    }

    public static Optional<BigDecimal> parseNumber(String value) {
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(new BigDecimal(value.replace(" ", "").replace(',', '.')));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Boolean> parseBoolean(String value) {
        if (value == null) {
            return Optional.empty();
        }
        String lower = value.trim().toLowerCase(Locale.ROOT);
        if (TRUE_VALUES.contains(lower)) {
            return Optional.of(true);
        }
        if (FALSE_VALUES.contains(lower)) {
            return Optional.of(false);
        }
        return Optional.empty();
    }

    public static List<String> splitList(String value) {
        if (value == null) {
            return List.of();
        }
        return Arrays.stream(value.split("[,;]"))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .distinct()
                .toList();
    }

    public static String display(SpecValueType valueType, String value, String unit) {
        if (valueType == SpecValueType.BOOLEAN) {
            return parseBoolean(value).map(b -> b ? "Да" : "Нет").orElse(value);
        }
        return unit == null || unit.isBlank() ? value : value + " " + unit;
    }
}
