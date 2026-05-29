package by.bsu.n1jel.pc.assembler.repository;

import by.bsu.n1jel.pc.assembler.entity.Producer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

import java.util.List;

public interface ProducerRepository extends JpaRepository<Producer, Long> {

    Page<Producer> findProducersByCountry(String country, Pageable pageable);

    @NativeQuery(
            """
                    SELECT DISTINCT country FROM producers
                    ORDER BY country DESC;
                    """
    )
    List<String> findAllProducerCountries();

    @NativeQuery(
            """
                    SELECT p.*,
                           (SELECT COUNT(*) FROM components c WHERE c.producer_id = p.id) AS components_count
                    FROM producers p
                    WHERE (SELECT COUNT(*) FROM components c WHERE c.producer_id = p.id) = (
                        SELECT MAX(component_count)
                        FROM (
                            SELECT COUNT(*) AS component_count
                            FROM components
                            GROUP BY producer_id
                        ) AS counts
                    )
                    ORDER BY components_count DESC
                    LIMIT :limit;
                    """
    )
    List<Producer> findTopProducers(Integer limit);
}
