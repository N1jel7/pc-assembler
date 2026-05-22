package by.bsu.n1jel.pc.assembler.dao;

import by.bsu.n1jel.pc.assembler.dto.request.search.ComponentSearchFilterRequestDto;
import by.bsu.n1jel.pc.assembler.entity.Component;
import by.bsu.n1jel.pc.assembler.entity.ComponentType;
import by.bsu.n1jel.pc.assembler.entity.Producer;
import by.bsu.n1jel.pc.assembler.repository.ComponentTypeRepository;
import by.bsu.n1jel.pc.assembler.repository.ProducerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static by.bsu.n1jel.pc.assembler.exception.common.ResourceExceptionFactory.componentTypeNotFoundException;
import static by.bsu.n1jel.pc.assembler.exception.common.ResourceExceptionFactory.producerNotFoundException;

@RequiredArgsConstructor
@Repository
public class SearchDao {

    @PersistenceContext
    private EntityManager entityManager;
    private final ProducerRepository producerRepository;
    private final ComponentTypeRepository componentTypeRepository;


    public Page<Component> searchComponents(ComponentSearchFilterRequestDto requestDto, Pageable pageable) {
        Session session = entityManager.unwrap(Session.class);
        HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        JpaCriteriaQuery<Component> componentCriteriaQuery = criteriaBuilder.createQuery(Component.class);
        Root<Component> root = componentCriteriaQuery.from(Component.class);
        List<Predicate> predicates = new ArrayList<>();

        if (requestDto.componentType() != null) {
            Predicate componentTypePredicate = criteriaBuilder.equal(root.get("componentType"), findComponentTypeById(requestDto.componentType()));
            predicates.add(componentTypePredicate);
        }

        if (requestDto.inStock() != null && requestDto.inStock()) {
            Predicate inStockPredicate = criteriaBuilder.greaterThan(root.get("inStock"), 0);
            predicates.add(inStockPredicate);
        }

        if (requestDto.name() != null) {
            Predicate namePredicate = criteriaBuilder.ilike(root.get("name"), requestDto.name() + "%");
            predicates.add(namePredicate);
        }

        if (requestDto.producer() != null) {
            Predicate producerPredicate = criteriaBuilder.equal(root.get("producer"), findProducerById(requestDto.producer()));
            predicates.add(producerPredicate);
        }

        if (requestDto.priceFrom() != null) {
            Predicate priceFromPredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("price"), requestDto.priceFrom());
            predicates.add(priceFromPredicate);
        }

        if (requestDto.priceTo() != null) {
            Predicate priceToPredicate = criteriaBuilder.lessThanOrEqualTo(root.get("price"), requestDto.priceTo());
            predicates.add(priceToPredicate);
        }

        componentCriteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        List<Component> result = entityManager
                .createQuery(componentCriteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = session
                .createQuery(componentCriteriaQuery.createCountQuery())
                .getSingleResult();

        return new PageImpl<>(result, pageable, count);
    }

    private Producer findProducerById(Long producerId) {
        return producerRepository.findById(producerId)
                .orElseThrow(
                        () -> producerNotFoundException(producerId)
                );
    }

    private ComponentType findComponentTypeById(Long componentTypeId) {
        return componentTypeRepository.findById(componentTypeId)
                .orElseThrow(
                        () -> componentTypeNotFoundException(componentTypeId)
                );
    }
}
