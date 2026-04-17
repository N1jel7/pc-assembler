package by.bsu.n1jel.pc.assembler.repository;

import by.bsu.n1jel.pc.assembler.entity.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecificationRepository extends JpaRepository<Specification, Long> {
    List<Specification> findSpecificationsByComponent_Id(Long componentId);
}
