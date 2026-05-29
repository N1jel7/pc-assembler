package by.bsu.n1jel.pc.assembler.repository;

import by.bsu.n1jel.pc.assembler.dto.response.StatsInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Build;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BuildRepository extends JpaRepository<Build, Long> {

    @Query(value = "SELECT calculate_build_cost(:buildId)", nativeQuery = true)
    BigDecimal getBuildPrice(@Param("buildId") Long buildId);

    @Query(value = "SELECT * FROM builds LIMIT :limit" ,nativeQuery = true)
    List<Build> findLatestBuilds(Integer limit);

    @Query(value = """
                SELECT 
                    (SELECT COUNT(*) FROM components) AS componentsCount,
                    (SELECT COUNT(*) FROM builds) AS buildsCount,
                    (SELECT COUNT(*) FROM producers) AS producersCount,
                    (SELECT COUNT(*) FROM specification_types) AS specificationTypesCount,
                    (SELECT COUNT(*) FROM build_partitions) AS buildItemsCount
            """, nativeQuery = true)
    StatsInfoResponseDto getStatistic();

    @Query(
            value = """
                SELECT 
                    (SELECT COUNT(*) FROM builds b
                    JOIN build_partitions on build_partitions.build_id = b.id
                    WHERE build_id = :buildId)
                    
                    
                    """, nativeQuery = true
    )
    Integer getBuildFilledSlotsAmount(Long buildId);
}
