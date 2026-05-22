package by.bsu.n1jel.pc.assembler.repository;

import by.bsu.n1jel.pc.assembler.entity.Build;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface BuildRepository extends JpaRepository<Build, Long> {

    @Query(value = "SELECT calculate_build_cost(:buildId)", nativeQuery = true)
    BigDecimal getBuildPrice(@Param("buildId") Long buildId);

}
