package com.wellsoft.pt.basicdata.datastore.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.basicdata.datastore.entity.ExecuteSqlDefinitionEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
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
@Repository
public class ExecuteSqlDefinitionDaoImpl extends
        AbstractJpaDaoImpl<ExecuteSqlDefinitionEntity, String> {
    public void deleteByIds(List<String> ids) {
        Map<String, Object> params = Maps.newHashMap();
        ((HashMap) params).put("ids", ids);
        deleteByHQL("delete ExecuteSqlDefinitionEntity where id in (:ids) ", params);
    }
}
