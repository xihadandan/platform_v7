package com.wellsoft.oauth2.repository;

import com.wellsoft.oauth2.entity.BaseEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

@Component
public class GeneralJpaRepository {

    @Resource
    private EntityManager entityManager;

    public static Long executeCountQuery(TypedQuery<Long> query) {
        Assert.notNull(query);
        List<Long> totals = query.getResultList();
        Long total = 0L;

        Long element;
        for (Iterator i$ = totals.iterator(); i$.hasNext(); total = total + (element == null ? 0L : element)) {
            element = (Long) i$.next();
        }

        return total;
    }

    @Transactional
    public void save(BaseEntity entity) {

        if (null == entity.getUuid()) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    @Transactional
    public void save(Iterable<? extends BaseEntity> entities) {

        if (entities == null) {
            return;
        }

        for (BaseEntity entity : entities) {
            save(entity);
        }
        return;
    }

    public BaseEntity findOne(Long uuid, Class clazz) {
        Assert.notNull(uuid, "The given id must not be null!");
        return (BaseEntity) entityManager.find(clazz, uuid);
    }

    @Transactional
    public void delete(Long uuid, Class clazz) {

        Assert.notNull(uuid, "The given id must not be null!");

        BaseEntity entity = findOne(uuid, clazz);

        if (entity == null) {
            throw new EmptyResultDataAccessException(
                    String.format("No %s entity with id %s exists!", clazz, uuid), 1);
        }

        delete(entity);
    }

    @Transactional
    public void delete(Long[] uuids, Class clazz) {

        Assert.notNull(uuids == null || uuids.length == 0, "The given id must not be null!");
        for (Long uid : uuids) {
            delete(uid, clazz);
        }
    }

    @Transactional
    public void delete(BaseEntity entity) {

        Assert.notNull(entity, "The entity must not be null!");
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    @Transactional
    public void delete(Iterable<? extends BaseEntity> entities) {

        Assert.notNull(entities, "The given Iterable of entities not be null!");

        for (BaseEntity entity : entities) {
            delete(entity);
        }
    }

    public Page<? extends BaseEntity> findAll(Specification<? extends BaseEntity> spec,
                                              Pageable pageable,
                                              Class clazz) {

        TypedQuery<? extends BaseEntity> query = getQuery(spec, pageable, clazz);
        return pageable == null ? new PageImpl(query.getResultList()) : readPage(query, pageable,
                spec, clazz);
    }

    protected TypedQuery<? extends BaseEntity> getQuery(Specification<? extends BaseEntity> spec,
                                                        Pageable pageable,
                                                        Class clazz) {

        Sort sort = pageable == null ? null : pageable.getSort();
        return getQuery(spec, sort, clazz);
    }

    protected Page<? extends BaseEntity> readPage(TypedQuery<? extends BaseEntity> query,
                                                  Pageable pageable,
                                                  Specification<? extends BaseEntity> spec,
                                                  Class clazz) {

        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        Long total = executeCountQuery(getCountQuery(spec, clazz));
        List content = total > pageable.getOffset() ? query.getResultList() : Collections.emptyList();

        return new PageImpl(content, pageable, total);
    }

    protected TypedQuery<Long> getCountQuery(Specification<? extends BaseEntity> spec,
                                             Class clazz) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root root = applySpecificationToCriteria(spec, query, clazz);

        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        return entityManager.createQuery(query);
    }

    protected TypedQuery<? extends BaseEntity> getQuery(Specification<? extends BaseEntity> spec,
                                                        Sort sort,
                                                        Class clazz) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<? extends BaseEntity> query = builder.createQuery(clazz);

        Root root = applySpecificationToCriteria(spec, query, clazz);
        query.select(root);

        if (sort != null) {
            query.orderBy(toOrders(sort, root, builder));
        }

        return entityManager.createQuery(query);
    }

    private Root<? extends BaseEntity> applySpecificationToCriteria(
            Specification<? extends BaseEntity> spec,
            CriteriaQuery<?> query, Class clazz) {

        Assert.notNull(query);
        Root root = query.from(clazz);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }

}