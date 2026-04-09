package by.bsu.n1jel.pc.assembler.repository;

import by.bsu.n1jel.pc.assembler.entity.Component;
import by.bsu.n1jel.pc.assembler.entity.ComponentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComponentRepository extends JpaRepository<Component, Long> {
    List<Component> findComponentsByComponentType(ComponentType componentType);
}
