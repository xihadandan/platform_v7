package com.wellsoft.pt.integration.dao;

import com.wellsoft.pt.integration.entity.DataOperationLog;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-5.1	ruanhg		2014-8-5		Create
 * </pre>
 * @date 2014-8-5
 */
public interface DataOperationLogDao extends JpaDao<DataOperationLog, String> {

    List<DataOperationLog> getDataOperationLogSynchronous();

    Boolean saveDataOperationLogOfNotEntity(String tableName, String uuid, Integer action);
}
