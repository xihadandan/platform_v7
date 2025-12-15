package com.wellsoft.oauth2.service;

import com.wellsoft.oauth2.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
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
public interface JpaService<T extends BaseEntity, ID extends Serializable, R extends JpaRepository> {


    /**
     * 保存
     *
     * @param t
     * @return
     */
    T save(T t);

    /**
     * 根据主键ID获取
     *
     * @param id
     * @return
     */
    T getOne(ID id);

    /**
     * 查询所有
     *
     * @return
     */
    List<T> listAll();

    /**
     * 分页查询所有
     *
     * @param page
     * @return
     */
    Page<T> listAll(PageRequest page);


    /**
     * 根据字段值匹配查询
     *
     * @param field
     * @param value
     * @return
     */
    T getBy(String field, Object value);

    /**
     * 根据实例样例匹配查询
     *
     * @param entity
     * @return
     */
    List<T> listByExample(T entity);

    void deleteEntity(T entity);

    void delete(ID id);

}
