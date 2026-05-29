package by.bsu.n1jel.pc.assembler.repository;

import by.bsu.n1jel.pc.assembler.entity.Component;
import by.bsu.n1jel.pc.assembler.entity.ComponentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ComponentRepository extends JpaRepository<Component, Long> {

    @Query(value = "SELECT * FROM components LIMIT :limit" ,nativeQuery = true)
    List<Component> findLatestComponents(Integer limit);

    List<Component> findComponentsByComponentType(ComponentType componentType);

    @Query(value = """
            SELECT DISTINCT p.name  FROM components c
            JOIN component_types cp ON c.component_type_id = cp.id
            JOIN producers p ON p.id = c.producer_id
            WHERE cp.name LIKE 'CPU' OR cp.name LIKE 'Процессор' OR cp.name LIKE 'Processor'
            ORDER BY p.name DESC;
            """,nativeQuery = true)
    List<String> getAllProcessorProducers();

}
