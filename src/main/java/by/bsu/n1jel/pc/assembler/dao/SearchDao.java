package by.bsu.n1jel.pc.assembler.dao;

import by.bsu.n1jel.pc.assembler.dto.request.search.BuildFilterRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.search.ComponentFilterRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.search.ProducerFilterRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.search.SpecificationTypeFilterRequestDto;
import by.bsu.n1jel.pc.assembler.entity.*;
import by.bsu.n1jel.pc.assembler.repository.BuildRepository;
import by.bsu.n1jel.pc.assembler.repository.ComponentTypeRepository;
import by.bsu.n1jel.pc.assembler.repository.ProducerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static by.bsu.n1jel.pc.assembler.exception.common.ResourceExceptionFactory.componentTypeNotFoundException;
import static by.bsu.n1jel.pc.assembler.exception.common.ResourceExceptionFactory.producerNotFoundException;

@RequiredArgsConstructor
@Repository
public class SearchDao {

    // TODO REFACTOR

    @PersistenceContext
    private EntityManager entityManager;
    private final ProducerRepository producerRepository;
    private final ComponentTypeRepository componentTypeRepository;

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    public Page<Build> searchBuilds(BuildFilterRequestDto requestDto, Pageable pageable) {
        Session session = getSession();

        StringBuilder hql = new StringBuilder("SELECT b FROM Build b WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if (requestDto.query() != null) {
            hql.append(" AND LOWER(b.name) LIKE LOWER(:query)");
            params.put("query", requestDto.query() + "%");
        }

        if (requestDto.priceFrom() != null || requestDto.priceTo() != null) {
            hql.append(" AND (SELECT SUM(bp.quantity * c.price) FROM BuildPartition bp JOIN bp.component c WHERE bp.build = b) ");

            if (requestDto.priceFrom() != null && requestDto.priceTo() != null) {
                hql.append(" BETWEEN :priceFrom AND :priceTo");
                params.put("priceFrom", requestDto.priceFrom());
                params.put("priceTo", requestDto.priceTo());
            } else if (requestDto.priceFrom() != null) {
                hql.append(" >= :priceFrom");
                params.put("priceFrom", requestDto.priceFrom());
            } else {
                hql.append(" <= :priceTo");
                params.put("priceTo", requestDto.priceTo());
            }
        }

        Query<Build> query = session.createQuery(hql.toString(), Build.class);
        params.forEach(query::setParameter);

        List<Build> result = query.setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        String countHql = hql.toString().replace("SELECT b FROM", "SELECT COUNT(b) FROM");
        Query<Long> countQuery = session.createQuery(countHql, Long.class);
        params.forEach(countQuery::setParameter);
        Long count = countQuery.getSingleResult();

        return new PageImpl<>(result, pageable, count);
    }

    public Page<SpecificationType> searchSpecificationTypes(SpecificationTypeFilterRequestDto requestDto, Pageable pageable) {
        Session session = getSession();
        HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        JpaCriteriaQuery<SpecificationType> specificationTypeCriteriaQuery = criteriaBuilder.createQuery(SpecificationType.class);
        Root<SpecificationType> root = specificationTypeCriteriaQuery.from(SpecificationType.class);
        List<Predicate> predicates = new ArrayList<>();

        if (requestDto.query() != null && !requestDto.query().isBlank()) {
            Predicate namePredicate = criteriaBuilder.ilike(root.get("name"), requestDto.query() + "%");
            predicates.add(namePredicate);
        }

        specificationTypeCriteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        List<SpecificationType> result = entityManager
                .createQuery(specificationTypeCriteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = session
                .createQuery(specificationTypeCriteriaQuery.createCountQuery())
                .getSingleResult();

        return new PageImpl<>(result, pageable, count);
    }

    public Page<Producer> searchProducers(ProducerFilterRequestDto requestDto, Pageable pageable) {
        Session session = getSession();
        HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        JpaCriteriaQuery<Producer> producerCriteriaQuery = criteriaBuilder.createQuery(Producer.class);
        Root<Producer> root = producerCriteriaQuery.from(Producer.class);
        List<Predicate> predicates = new ArrayList<>();


        if (requestDto.query() != null && !requestDto.query().isBlank()) {
            Predicate namePredicate = criteriaBuilder.ilike(root.get("name"), requestDto.query() + "%");
            predicates.add(namePredicate);
        }

        if (requestDto.country() != null && !requestDto.country().isBlank()) {
            Predicate countryPredicate = criteriaBuilder.equal(root.get("country"), requestDto.country());
            predicates.add(countryPredicate);
        }

        producerCriteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        List<Producer> result = entityManager
                .createQuery(producerCriteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = session
                .createQuery(producerCriteriaQuery.createCountQuery())
                .getSingleResult();

        return new PageImpl<>(result, pageable, count);
    }

    public Page<Component> searchComponents(ComponentFilterRequestDto requestDto, Pageable pageable) {
        Session session = getSession();
        HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        JpaCriteriaQuery<Component> componentCriteriaQuery = criteriaBuilder.createQuery(Component.class);
        Root<Component> root = componentCriteriaQuery.from(Component.class);
        List<Predicate> predicates = new ArrayList<>();

        if (requestDto.componentType() != null) {
            Predicate componentTypePredicate = criteriaBuilder.equal(root.get("componentType"), findComponentTypeById(requestDto.componentType()));
            predicates.add(componentTypePredicate);
        }

        if (requestDto.inStock() != null && requestDto.inStock()) {
            Predicate inStockPredicate = criteriaBuilder.greaterThan(root.get("stockQuantity"), 0);
            predicates.add(inStockPredicate);
        }

        if (requestDto.query() != null && !requestDto.query().isBlank()) {
            Predicate namePredicate = criteriaBuilder.ilike(root.get("name"), requestDto.query() + "%");
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
