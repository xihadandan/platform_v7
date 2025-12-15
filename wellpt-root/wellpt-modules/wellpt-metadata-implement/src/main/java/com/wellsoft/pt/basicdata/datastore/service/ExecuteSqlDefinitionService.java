package com.wellsoft.pt.basicdata.datastore.service;

import com.wellsoft.pt.basicdata.datastore.dao.impl.ExecuteSqlDefinitionDaoImpl;
import com.wellsoft.pt.basicdata.datastore.entity.ExecuteSqlDefinitionEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/9/4
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/4    chenq		2018/9/4		Create
 * </pre>
 */
public interface ExecuteSqlDefinitionService extends
        JpaService<ExecuteSqlDefinitionEntity, ExecuteSqlDefinitionDaoImpl, String> {

    String getSqlById(String id);

    ExecuteSqlDefinitionEntity getById(String id);

    void deleteByIds(List<String> ids);


    int executeUpdate(String sql, Map<String, Object> params);

    List executeQrySQL(String sql, Map<String, Object> params);
}
