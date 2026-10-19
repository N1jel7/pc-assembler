package by.bsu.n1jel.pc.assembler;

import by.bsu.n1jel.pc.assembler.dto.response.StatsInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.StatisticService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/** Liquibase-миграции применяются на чистую БД, тестовые данные и функции на месте. */
class DatabaseMigrationTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StatisticService statisticService;

    @Test
    void allChangesetsApplied() {
        Integer applied = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM databasechangelog", Integer.class);
        assertThat(applied).isEqualTo(5);
    }

    @Test
    void seedDataLoaded() {
        StatsInfoResponseDto stats = statisticService.getStatistics();

        assertThat(stats.componentsCount()).isPositive();
        assertThat(stats.buildsCount()).isPositive();
        assertThat(stats.producersCount()).isPositive();
        assertThat(stats.specificationTypesCount()).isPositive();
        assertThat(stats.buildItemsCount()).isPositive();
    }

    @Test
    void calculateBuildCostMatchesManualSum() {
        Long buildId = jdbcTemplate.queryForObject(
                "SELECT id FROM builds WHERE name = 'Бюджетный игровой ПК'", Long.class);

        BigDecimal fromFunction = jdbcTemplate.queryForObject(
                "SELECT calculate_build_cost(?)", BigDecimal.class, buildId);
        BigDecimal manual = jdbcTemplate.queryForObject("""
                SELECT SUM(bp.quantity * c.price) FROM build_partitions bp
                JOIN components c ON c.id = bp.component_id
                WHERE bp.build_id = ?
                """, BigDecimal.class, buildId);

        assertThat(fromFunction).isPositive().isEqualByComparingTo(manual);
    }

    @Test
    void calculateBuildCostOfUnknownBuildIsZero() {
        BigDecimal cost = jdbcTemplate.queryForObject("SELECT calculate_build_cost(-1)", BigDecimal.class);
        assertThat(cost).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
