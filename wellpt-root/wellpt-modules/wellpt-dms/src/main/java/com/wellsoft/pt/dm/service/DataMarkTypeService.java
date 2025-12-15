package com.wellsoft.pt.dm.service;

import com.wellsoft.pt.dm.dao.impl.DataMarkTypeDaoImpl;
import com.wellsoft.pt.dm.entity.DataMarkTypeEntity;
import com.wellsoft.pt.jpa.service.JpaService;

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
public interface DataMarkTypeService extends JpaService<DataMarkTypeEntity, DataMarkTypeDaoImpl, Long> {

    void markData(String dataUuid, DataMarkTypeEntity.Type type);

    void markData(List<String> dataUuid, DataMarkTypeEntity.Type type);

    Map<String, DataMarkTypeEntity.Type> getDataTypes(List<String> dataUuids);


    void deleteMarkData(String dataUuid, List<DataMarkTypeEntity.Type> type);

    void deleteMarkData(List<String> dataUuid, List<DataMarkTypeEntity.Type> type);

}
