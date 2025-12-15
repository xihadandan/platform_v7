package com.wellsoft.pt.repository.service.impl;

import com.wellsoft.pt.repository.service.FieldService;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.util.Iterator;

/**
 * @author lilin
 * @ClassName: DefaultFieldManager
 * @Description: 默认的字段处理类
 */
@Service
public class FieldServiceImpl implements FieldService {

    /**
     * @param @param table
     * @param @param dialect
     * @param @param mapping    设定文件
     * @return void    返回类型
     * @throws
     * @Title: save
     * @Description: 通过hibernate 的配置文件等 直接生成相应的field配置
     */
    @Override
    public void saveTableField(Table table, Dialect dialect, Mapping mapping) {
        Iterator iterfield = table.getColumnIterator();
        while (iterfield.hasNext()) {
            Column column = (Column) iterfield.next();
        }
    }

    /**
     * @param @param clazz    设定文件
     * @return void    返回类型
     * @throws
     * @Title: save
     * @Description: 根据实体类直接生成相应的field保存数据库
     */
    @Override
    public void saveEntityField(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Entity entity = (Entity) clazz.getAnnotation(Entity.class);
        if (null == entity) {
            return;
        }
        for (Field field : fields) {
            String fieldName = field.getName();
            Class typeclass = field.getType();
        }

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.service.FieldService#deleteField(java.lang.String)
     */
    @Override
    public void deleteField(String id) {
        // TODO Auto-generated method stub

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.service.FieldService#isIndex(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isIndex(String entityName, String fileldname) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.service.FieldService#isShow(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isShow(String entityName, String fileldname) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.service.FieldService#isSort(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isSort(String entityName, String fileldname) {
        // TODO Auto-generated method stub
        return false;
    }
}
