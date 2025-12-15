package com.wellsoft.pt.integration.service;

import com.wellsoft.pt.integration.bean.FtpDataBean;
import com.wellsoft.pt.integration.entity.DataOperationLog;
import com.wellsoft.pt.integration.entity.SynchronousSourceTable;
import com.wellsoft.pt.task.entity.JobDetails;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-9.1	ruanhg		2014-7-9		Create
 * </pre>
 * @date 2014-7-9
 */
public interface ExchangeDataSynchronousService {

    public SynchronousSourceTable getBeanByUuid(String uuid);

    public List<Map<String, Object>> getColumnsData(String type, String definitionUuid);

    public List getSynchronousSourceTablesByType(String treeNodeId, String type);

    public Boolean delSynchronousSourceTables(String[] uuids);

    public Boolean saveBean(SynchronousSourceTable bean);

    public Boolean synchronousOutData(Date preFireTime, String taskUuid);

    public Boolean synchronousInData(String taskUuid);

    public Boolean synchronousDataDelDb();

    public List<JobDetails> getJobDetails();

    public Boolean saveDataOperationLog(DataOperationLog dataOperationLog);

    public void logEntityInterceptorTool(Object entity, Serializable id, Object[] state, String[] propertyNames,
                                         Type[] types, Integer methodNum);

    public Boolean saveEntityObj(Object obj, String entityName);

    Object deserializationToObject(String serStr);

    public Boolean newSynchronousOutData();

    public Boolean newSynchronousInData();

    void test_logEntityInterceptorTool(Object entity, Serializable id, Object[] state, String[] propertyNames,
                                       Type[] types, Integer methodNum);

    public Boolean backData(DataOperationLog d, FtpDataBean ftpDataBean);

}
