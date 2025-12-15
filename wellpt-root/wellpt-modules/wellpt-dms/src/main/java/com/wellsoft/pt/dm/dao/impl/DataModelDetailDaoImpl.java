package com.wellsoft.pt.dm.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dm.entity.DataModelDetailEntity;
import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年04月06日   chenq	 Create
 * </pre>
 */
@Repository
public class DataModelDetailDaoImpl extends AbstractJpaDaoImpl<DataModelDetailEntity, Long> implements JpaDao<DataModelDetailEntity, Long> {
    public void deleteByDataModelUuid(Long uuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("dataModelUuid", uuid);
        this.deleteByHQL("delete from DataModelDetailEntity where dataModelUuid=:dataModelUuid", param);
    }
}
