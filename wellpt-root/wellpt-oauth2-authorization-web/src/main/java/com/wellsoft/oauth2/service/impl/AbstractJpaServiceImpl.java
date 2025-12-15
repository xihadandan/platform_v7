package com.wellsoft.oauth2.service.impl;

import com.wellsoft.oauth2.entity.BaseEntity;
import com.wellsoft.oauth2.service.JpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/10    chenq		2019/9/10		Create
 * </pre>
 */
public abstract class AbstractJpaServiceImpl<T extends BaseEntity, ID extends Serializable, R extends JpaRepository> implements
        JpaService<T, ID, R> {

    @Autowired
    protected R repository;
    private Class<T> entityClass;

    public AbstractJpaServiceImpl() {
        try {
            Type genType = this.getClass().getGenericSuperclass();
            if (genType instanceof ParameterizedType) {
                Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                entityClass = (Class<T>) params[0];
            }

        } catch (Exception e) {

        }
    }

    @Override
    @Transactional
    public T save(T t) {
        return (T) repository.save(t);
    }

    @Override
    public T getOne(ID id) {
        return (T) repository.findOne(id);
    }

    @Override
    public List listAll() {
        return repository.findAll();
    }

    @Override
    public Page listAll(PageRequest page) {
        return repository.findAll(page);
    }

    @Override
    public T getBy(String field, Object value) {
        try {
            T entity = entityClass.newInstance();
            Field f = ReflectionUtils.findField(entityClass, field);
            f.setAccessible(true);
            f.set(entity, value);
            Example example = Example.of(entity, ExampleMatcher.matching());
            return (T) repository.findOne(example);
        } catch (Exception e) {
            throw new NotFoundException("entity data not found");
        }
    }

    @Override
    public List<T> listByExample(T entity) {
        try {
            Example example = Example.of(entity);
            Example e = Example.of(entity, ExampleMatcher.matchingAny());
            return repository.findAll(example);
        } catch (Exception e) {
            throw new NotFoundException("entity data not found");
        }
    }

    @Override
    @Transactional
    public void delete(ID id) {
        repository.delete(id);
    }

    @Override
    @Transactional
    public void deleteEntity(T entity) {
        repository.delete(entity.getUuid());
    }
}
