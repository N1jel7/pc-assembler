package by.bsu.n1jel.pc.assembler.compatibility;

import by.bsu.n1jel.pc.assembler.compatibility.rule.*;
import by.bsu.n1jel.pc.assembler.dto.response.CompatibilityIssueDto;
import by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static by.bsu.n1jel.pc.assembler.compatibility.CompatibilitySeverity.*;
import static by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode.*;
import static org.assertj.core.api.Assertions.assertThat;

class CompatibilityRulesTest {

    private static final List<CompatibilityRule> RULES = List.of(
            new SocketRule(), new CpuCoolerRule(), new MemoryRule(), new CaseFitRule(),
            new PowerRule(), new StoragePortsRule(), new CompletenessRule(), new QuantityRule(), new PcieRule());

    private static final AtomicLong IDS = new AtomicLong();

    private static BuildPart part(ComponentTypeCode type, String... keyValues) {
        Map<String, String> specs = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            specs.put(keyValues[i], keyValues[i + 1]);
        }
        return new BuildPart(IDS.incrementAndGet(), type.name(), type, 1, 10, 1, 8, specs);
    }

    private static BuildPart with(BuildPart part, String key, String value) {
        Map<String, String> specs = new HashMap<>(part.specs());
        if (value == null) {
            specs.remove(key);
        } else {
            specs.put(key, value);
        }
        return new BuildPart(part.componentId(), part.name(), part.typeCode(), part.quantity(),
                part.stockQuantity(), part.minAmount(), part.maxAmount(), specs);
    }

    private static BuildPart quantity(BuildPart part, int quantity) {
        return new BuildPart(part.componentId(), part.name(), part.typeCode(), quantity,
                part.stockQuantity(), part.minAmount(), part.maxAmount(), part.specs());
    }

    private static BuildPart stock(BuildPart part, int stock) {
        return new BuildPart(part.componentId(), part.name(), part.typeCode(), part.quantity(),
                stock, part.minAmount(), part.maxAmount(), part.specs());
    }

    private final BuildPart cpu = part(CPU, "socket", "AM5", "supported_memory_types", "DDR5",
            "max_memory_gb", "192", "max_memory_speed_mts", "5200", "tdp_w", "120", "integrated_graphics", "true");
    private final BuildPart board = part(MOTHERBOARD, "socket", "AM5", "memory_type", "DDR5", "ram_slots", "4",
            "max_memory_gb", "192", "max_memory_speed_mts", "6600", "form_factor", "ATX",
            "m2_slots", "4", "sata_ports", "6", "pcie_version", "5");
    private final BuildPart ram = part(RAM, "memory_type", "DDR5", "capacity_gb", "32",
            "memory_speed_mts", "5200", "memory_modules", "2");
    private final BuildPart gpu = part(GPU, "tdp_w", "285", "length_mm", "305", "pcie_version", "4");
    private final BuildPart ssd = part(SSD, "storage_interface", "M.2 NVMe", "capacity_gb", "1000");
    private final BuildPart psu = part(PSU, "wattage_w", "850", "form_factor", "ATX");
    private final BuildPart pcCase = part(CASE, "case_mb_form_factors", "E-ATX, ATX, mATX",
            "case_psu_form_factors", "ATX", "case_max_gpu_length_mm", "400",
            "case_max_cooler_height_mm", "170", "case_max_radiator_mm", "360");
    private final BuildPart cooler = part(CPU_COOLER, "cooler_sockets", "LGA1700, AM5",
            "cooler_max_tdp_w", "260", "height_mm", "160");

    private List<BuildPart> fullBuild() {
        return new ArrayList<>(List.of(cpu, board, ram, gpu, ssd, psu, pcCase, cooler));
    }

    private static List<CompatibilityIssueDto> check(List<BuildPart> parts) {
        Map<String, String> names = Map.of("socket", "Сокет", "height_mm", "Высота");
        CompatibilityContext context = new CompatibilityContext(parts, names);
        RULES.forEach(rule -> rule.check(context));
        return context.issues();
    }

    private static List<BuildPart> replace(List<BuildPart> parts, BuildPart replacement) {
        List<BuildPart> result = new ArrayList<>(parts);
        result.replaceAll(part -> part.componentId().equals(replacement.componentId()) ? replacement : part);
        return result;
    }

    private static List<CompatibilityIssueDto> only(List<CompatibilityIssueDto> issues, CompatibilitySeverity severity) {
        return issues.stream().filter(issue -> issue.severity() == severity).toList();
    }

    @Test
    void compatibleBuildHasNoIssues() {
        assertThat(check(fullBuild())).isEmpty();
    }

    @Test
    void socketMismatch() {
        List<CompatibilityIssueDto> issues = check(replace(fullBuild(), with(board, "socket", "LGA1700")));

        assertThat(only(issues, ERROR))
                .anySatisfy(issue -> {
                    assertThat(issue.rule()).isEqualTo("socket");
                    assertThat(issue.message()).contains("AM5", "LGA1700");
                    assertThat(issue.componentIds()).containsExactlyInAnyOrder(cpu.componentId(), board.componentId());
                });
    }

    @Test
    void socketComparisonIgnoresCase() {
        assertThat(check(replace(fullBuild(), with(board, "socket", "am5")))).isEmpty();
    }

    @Test
    void coolerDoesNotFitSocketAndIsTooWeak() {
        BuildPart weakIntelCooler = with(with(cooler, "cooler_sockets", "LGA1700"), "cooler_max_tdp_w", "95");

        List<CompatibilityIssueDto> issues = check(replace(fullBuild(), weakIntelCooler));

        assertThat(only(issues, ERROR)).extracting(CompatibilityIssueDto::message)
                .anyMatch(message -> message.contains("не крепится на сокет AM5"));
        assertThat(only(issues, WARNING)).extracting(CompatibilityIssueDto::message)
                .anyMatch(message -> message.contains("рассчитан на 95 Вт"));
    }

    @Test
    void memoryTypeMustMatchBoardAndCpu() {
        BuildPart ddr4 = with(ram, "memory_type", "DDR4");

        List<CompatibilityIssueDto> errors = only(check(replace(fullBuild(), ddr4)), ERROR);

        assertThat(errors).extracting(CompatibilityIssueDto::message)
                .anyMatch(message -> message.contains("поддерживает только DDR5"))
                .anyMatch(message -> message.contains("Процессор не работает с памятью DDR4"));
    }

    @Test
    void cpuSupportingBothMemoryTypesAcceptsDdr4() {
        BuildPart intelCpu = with(cpu, "supported_memory_types", "DDR4, DDR5");
        List<BuildPart> parts = replace(replace(fullBuild(), intelCpu), with(board, "memory_type", "DDR4"));

        assertThat(only(check(replace(parts, with(ram, "memory_type", "DDR4"))), ERROR)).isEmpty();
    }

    @Test
    void tooManyMemoryModules() {
        // 3 комплекта по 2 планки = 6 планок в 4 слота
        List<CompatibilityIssueDto> errors = only(check(replace(fullBuild(), quantity(ram, 3))), ERROR);

        assertThat(errors).extracting(CompatibilityIssueDto::message)
                .anyMatch(message -> message.equals("6 планок памяти не поместятся: у материнской платы 4 слота"));
    }

    @Test
    void singleModuleWarnsAboutSingleChannel() {
        List<CompatibilityIssueDto> issues = check(replace(fullBuild(), with(ram, "memory_modules", "1")));

        assertThat(only(issues, ERROR)).isEmpty();
        assertThat(only(issues, WARNING)).extracting(CompatibilityIssueDto::message)
                .anyMatch(message -> message.contains("одноканальном"));
    }

    @Test
    void memoryCapacityOverLimit() {
        BuildPart bigKit = with(ram, "capacity_gb", "128");
        List<BuildPart> parts = replace(replace(fullBuild(), quantity(bigKit, 2)), with(board, "max_memory_gb", "192"));

        assertThat(only(check(parts), ERROR)).extracting(CompatibilityIssueDto::message)
                .anyMatch(message -> message.contains("Всего 256 ГБ памяти"));
    }

    @Test
    void fasterMemoryIsLimitedBySlowestPart() {
        List<CompatibilityIssueDto> issues = check(replace(fullBuild(), with(ram, "memory_speed_mts", "6000")));

        assertThat(only(issues, ERROR)).isEmpty();
        assertThat(only(issues, WARNING)).extracting(CompatibilityIssueDto::message)
                .containsExactly("Память 6000 МТ/с будет работать на 5200 МТ/с — это максимум для «CPU»");
    }

    @Test
    void caseFit() {
        List<BuildPart> parts = fullBuild();
        parts = replace(parts, with(board, "form_factor", "XL-ATX"));
        parts = replace(parts, with(gpu, "length_mm", "420"));
        parts = replace(parts, with(cooler, "height_mm", "175"));
        parts = replace(parts, with(psu, "form_factor", "SFX-L"));

        assertThat(only(check(parts), ERROR)).extracting(CompatibilityIssueDto::message)
                .anyMatch(message -> message.contains("Материнская плата формата XL-ATX не помещается"))
                .anyMatch(message -> message.contains("Видеокарта длиной 420 мм не помещается"))
                .anyMatch(message -> message.contains("Кулер высотой 175 мм не помещается"))
                .anyMatch(message -> message.contains("Блок питания формата SFX-L не помещается"));
    }

    @Test
    void liquidCoolerChecksRadiatorInsteadOfHeight() {
        BuildPart aio = with(with(cooler, "height_mm", null), "radiator_mm", "420");

        assertThat(only(check(replace(fullBuild(), aio)), ERROR)).extracting(CompatibilityIssueDto::message)
                .anyMatch(message -> message.contains("Радиатор СЖО 420 мм не помещается"));
    }

    @Test
    void powerSupplyTooWeak() {
        // 120 + 285 + 100 = 505 Вт
        List<CompatibilityIssueDto> errors = only(check(replace(fullBuild(), with(psu, "wattage_w", "450"))), ERROR);

        assertThat(errors).extracting(CompatibilityIssueDto::message)
                .containsExactly("Блока питания 450 Вт не хватит: сборка потребляет около 505 Вт");
    }

    @Test
    void powerSupplyNearLimitWarnsWithRecommendation() {
        List<CompatibilityIssueDto> issues = check(replace(fullBuild(), with(psu, "wattage_w", "550")));

        assertThat(only(issues, ERROR)).isEmpty();
        // 505 / 0.8 = 632 → округляем вверх до 650
        assertThat(only(issues, WARNING)).extracting(CompatibilityIssueDto::message)
                .anyMatch(message -> message.contains("лучше от 650 Вт"));
    }

    @Test
    void notEnoughM2Slots() {
        List<BuildPart> parts = replace(replace(fullBuild(), quantity(ssd, 3)), with(board, "m2_slots", "2"));

        assertThat(only(check(parts), ERROR)).extracting(CompatibilityIssueDto::message)
                .containsExactly("Накопителей M.2: 3, а на материнской плате 2 слота M.2");
    }

    @Test
    void emptyBuildIsIncomplete() {
        List<CompatibilityIssueDto> issues = check(List.of());

        assertThat(issues).allMatch(issue -> issue.severity() == INCOMPLETE);
        assertThat(issues).hasSize(6);
    }

    @Test
    void cpuWithoutGraphicsNeedsGpu() {
        List<BuildPart> parts = replace(fullBuild(), with(cpu, "integrated_graphics", "false"));
        parts.remove(gpu);

        assertThat(only(check(parts), INCOMPLETE)).extracting(CompatibilityIssueDto::message)
                .containsExactly("Нужна видеокарта: у процессора нет встроенной графики");
    }

    @Test
    void quantityOutOfRangeAndStock() {
        BuildPart tooMany = new BuildPart(cpu.componentId(), "CPU", CPU, 2, 1, 1, 1, cpu.specs());
        List<CompatibilityIssueDto> issues = check(replace(replace(fullBuild(), tooMany), stock(gpu, 0)));

        assertThat(only(issues, ERROR)).extracting(CompatibilityIssueDto::message)
                .anyMatch(message -> message.equals("«CPU»: в сборке 2 шт., а допустимо от 1 до 1"));
        assertThat(only(issues, WARNING)).extracting(CompatibilityIssueDto::message)
                .anyMatch(message -> message.equals("«CPU»: в сборке 2 шт., а на складе только 1"))
                .anyMatch(message -> message.equals("«GPU» нет на складе"));
    }

    @Test
    void missingDataIsWarningNotSilentPass() {
        List<CompatibilityIssueDto> issues = check(replace(fullBuild(), with(board, "socket", null)));

        assertThat(only(issues, ERROR)).isEmpty();
        // о пропущенном сокете платы сообщаем один раз, хотя он нужен нескольким проверкам
        assertThat(only(issues, WARNING)).extracting(CompatibilityIssueDto::message)
                .containsExactly("Не удалось проверить совместимость: у «MOTHERBOARD» не указано «Сокет»");
    }

    @Test
    void newerGpuPcieOnOlderBoardIsWarning() {
        List<CompatibilityIssueDto> issues = check(replace(fullBuild(), with(board, "pcie_version", "3")));

        assertThat(only(issues, ERROR)).isEmpty();
        assertThat(only(issues, WARNING)).extracting(CompatibilityIssueDto::message)
                .anyMatch(message -> message.contains("PCIe 4 будет работать в режиме PCIe 3"));
    }
}
