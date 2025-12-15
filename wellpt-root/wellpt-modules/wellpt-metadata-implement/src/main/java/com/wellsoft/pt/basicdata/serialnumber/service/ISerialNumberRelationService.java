package com.wellsoft.pt.basicdata.serialnumber.service;

import com.wellsoft.pt.basicdata.serialnumber.dao.ISerialNumberRelationDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberRelation;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.xxljob.model.ExecutionParam;

/**
 * @Auther: yt
 * @Date: 2022/4/26 14:54
 * @Description:
 */
public interface ISerialNumberRelationService extends JpaService<SerialNumberRelation, ISerialNumberRelationDao, String> {

    void dataCleaningTask(ExecutionParam executionParam);

    boolean existsTableFieldName(String tableName, String fieldName);

    boolean existsTable(String tableName);
}
