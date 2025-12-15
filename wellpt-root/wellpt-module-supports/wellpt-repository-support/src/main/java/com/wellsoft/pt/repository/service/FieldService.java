package com.wellsoft.pt.repository.service;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.Table;

/**
 * @author lilin
 * @ClassName: FieldManager
 * @Description: 定义fieldmanager用于处理所有字段的定义
 */
public interface FieldService {

    public void deleteField(String id);

    public boolean isIndex(String entityName, String fileldname);

    public boolean isShow(String entityName, String fileldname);

    public boolean isSort(String entityName, String fileldname);

    public void saveEntityField(Class clazz);

    public void saveTableField(Table table, Dialect dialect, Mapping mapping);

}
