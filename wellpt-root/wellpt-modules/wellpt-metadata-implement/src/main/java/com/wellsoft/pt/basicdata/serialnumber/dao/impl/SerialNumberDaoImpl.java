package com.wellsoft.pt.basicdata.serialnumber.dao.impl;

import com.wellsoft.pt.basicdata.serialnumber.dao.SerialNumberDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumber;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description: 流水号定义数据层访问类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-6.1	zhouyq		2013-3-6		Create
 * </pre>
 * @date 2013-3-6
 */
@Repository
public class SerialNumberDaoImpl extends AbstractJpaDaoImpl<SerialNumber, String> implements SerialNumberDao {

    /**
     * 通过流水号字段名查找所有流水号
     *
     * @param fieldName
     * @param entity
     */
    @Override
    public List<String> findByFieldName(String fieldName, Object entity) {
        String hql = "select " + fieldName + " from " + entity;
        Query query = getSession().createQuery(hql);
        List<String> list = query.list();
        return list;
    }

    @Override
    public SerialNumber getById(String id) {
        return getOneByHQL("from SerialNumber where id='" + id + "'", null);
    }

    @Override
    public SerialNumber getByName(String name) {
        return getOneByHQL("from SerialNumber where name='" + name + "'", null);
    }
}
