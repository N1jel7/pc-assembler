package by.bsu.n1jel.pc.assembler.repository;

import by.bsu.n1jel.pc.assembler.entity.ComponentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ComponentTypeRepository extends JpaRepository<ComponentType, Long> {
    @Query(value = """
        SELECT name FROM component_types WHERE id = :id
        """, nativeQuery = true)
    String getComponentTypeNameById(Long id);

    @Query(value = """
        SELECT COUNT(id) FROM component_types
        """, nativeQuery = true)
    Integer getAllComponentTypesSize();
}
