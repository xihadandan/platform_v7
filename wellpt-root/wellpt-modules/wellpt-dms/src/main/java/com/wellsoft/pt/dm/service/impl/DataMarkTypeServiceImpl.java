package com.wellsoft.pt.dm.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.dm.dao.impl.DataMarkTypeDaoImpl;
import com.wellsoft.pt.dm.entity.DataMarkTypeEntity;
import com.wellsoft.pt.dm.service.DataMarkTypeService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年04月13日   chenq	 Create
 * </pre>
 */
@Service
public class DataMarkTypeServiceImpl extends AbstractJpaServiceImpl<DataMarkTypeEntity, DataMarkTypeDaoImpl, Long> implements DataMarkTypeService {
    @Override
    @Transactional
    public void markData(String dataUuid, DataMarkTypeEntity.Type type) {
        DataMarkTypeEntity typeEntity = new DataMarkTypeEntity();
        typeEntity.setDataUuid(dataUuid);
        typeEntity.setType(type);
        if (CollectionUtils.isEmpty(this.dao.listByEntity(typeEntity))) {
            this.dao.save(typeEntity);
        }
    }

    @Override
    @Transactional
    public void markData(List<String> dataUuid, DataMarkTypeEntity.Type type) {
        for (String u : dataUuid) {
            this.deleteMarkData(u, null);
            this.markData(u, type);
        }
    }

    @Override
    public Map<String, DataMarkTypeEntity.Type> getDataTypes(List<String> dataUuids) {
        List<DataMarkTypeEntity> types = this.dao.listByFieldInValues("dataUuid", dataUuids);
        Map<String, DataMarkTypeEntity.Type> typeMap = Maps.newHashMap();
        for (DataMarkTypeEntity t : types) {
            typeMap.put(t.getDataUuid(), t.getType());
        }
        return typeMap;
    }

    @Override
    @Transactional
    public void deleteMarkData(String dataUuid, List<DataMarkTypeEntity.Type> type) {
        this.deleteMarkData(Lists.newArrayList(dataUuid), type);
    }

    @Override
    @Transactional
    public void deleteMarkData(List<String> dataUuid, List<DataMarkTypeEntity.Type> type) {
        StringBuilder string = new StringBuilder("delete from DataMarkTypeEntity where ");
        if (dataUuid.size() == 1) {
            string.append(" dataUuid = :dataUuid ");
        } else {
            string.append(" dataUuid in (:dataUuid) ");
        }
        if (CollectionUtils.isNotEmpty(type)) {
            string.append(" and type in (:type)");
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataUuid", dataUuid);
        params.put("type", type);
        this.dao.deleteByHQL(string.toString(), params);
    }
}
