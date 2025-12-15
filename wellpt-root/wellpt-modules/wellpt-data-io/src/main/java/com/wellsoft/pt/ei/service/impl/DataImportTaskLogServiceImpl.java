package com.wellsoft.pt.ei.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.ei.bo.DataRecordDetailBo;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dao.DataImportTaskLogDao;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;
import com.wellsoft.pt.ei.service.DataImportTaskLogService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 数据导入任务日志service实现类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/9/16.1	liuyz		2021/9/16		Create
 * </pre>
 * @date 2021/9/16
 */
@Service
@Transactional
public class DataImportTaskLogServiceImpl extends AbstractJpaServiceImpl<DataImportTaskLog, DataImportTaskLogDao, String> implements DataImportTaskLogService {
    @Override
    public List<DataImportTaskLog> listByTaskUuids(List<String> taskUuids) {
        if (taskUuids == null || taskUuids.size() == 0) {
            return null;
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("taskUuids", taskUuids);
        return listByHQL("from DataImportTaskLog where taskUuid in (:taskUuids)", paramMap);
    }

    @Override
    public DataImportTaskLog getBySourceUuid(String sourceUuid) {
        DataImportTaskLog dataImportTaskLog = new DataImportTaskLog();
        dataImportTaskLog.setSourceUuid(sourceUuid);
        return getNormalLog(dataImportTaskLog);
    }

    @Override
    public DataImportTaskLog getBySourceId(String sourceId) {
        DataImportTaskLog dataImportTaskLog = new DataImportTaskLog();
        dataImportTaskLog.setSourceId(sourceId);
        return getNormalLog(dataImportTaskLog);
    }

    public long countByStatus(String taskUuid, int status) {
        DataImportTaskLog dataImportTaskLog = new DataImportTaskLog();
        dataImportTaskLog.setTaskUuid(taskUuid);
        dataImportTaskLog.setImportStatus(status);
        return dao.countByEntity(dataImportTaskLog);
    }

    private DataImportTaskLog getNormalLog(DataImportTaskLog dataImportTaskLog) {
        List<DataImportTaskLog> dataImportTaskLogs = this.dao.listByEntityAndPage(dataImportTaskLog, null, "createTime desc");
        if (null != dataImportTaskLogs && !dataImportTaskLogs.isEmpty()) {
            for (DataImportTaskLog log : dataImportTaskLogs) {
                // 返回导入正常 的对象
                if (log.getImportStatus() != null && log.getImportStatus().intValue() == DataExportConstants.DATA_LOG_STATUS_NORMAL.intValue()) {
                    if (StringUtils.isNotBlank(log.getAfterImportUuid()) || StringUtils.isNotBlank(log.getAfterImportId())) {
                        return log;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<DataImportTaskLog> listErrorByTaskUuids(List<String> taskUuids) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("taskUuids", taskUuids);
        return listByHQL("from DataImportTaskLog where importStatus = 0 AND taskUuid in (:taskUuids)", paramMap);
    }

    @Override
    public DataRecordDetailBo countChild(String taskUuid, String type) {
        DataRecordDetailBo detailBo = new DataRecordDetailBo();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("taskUuid", taskUuid);
        if (DataExportConstants.DATA_TYPE_COUNT_REPEAT.equals(type)) {
            paramMap.put("isRepeat", 1);
            detailBo.setName("重复数据");
        } else if (DataExportConstants.DATA_TYPE_COUNT_EXCEPTION.equals(type)) {
            paramMap.put("importStatus", 0);
            detailBo.setName("异常数据");
        } else {
            paramMap.put("import", "import");
            detailBo.setName("导入数据");
        }
        List<DataRecordDetailBo> dataRecordDetailBos = getDao().listItemByNameSQLQuery("importChildCount", DataRecordDetailBo.class, paramMap, null);

        List<String> sortList = Lists.newArrayList("组织架构节点", "用户", "群组", "组织类型", "职务", "职级");
        Collections.sort(dataRecordDetailBos, new Comparator<DataRecordDetailBo>() {
            @Override
            public int compare(DataRecordDetailBo o1, DataRecordDetailBo o2) {
                return new Integer(sortList.indexOf(o1.getName())).compareTo(sortList.indexOf(o2.getName()));
            }
        });

        detailBo.setChildDetails(dataRecordDetailBos);

        if (CollectionUtils.isNotEmpty(dataRecordDetailBos)) {
            detailBo.setCount(0L);
        }

        dataRecordDetailBos.forEach(bo -> detailBo.setCount(detailBo.getCount() + bo.getCount()));
        return detailBo;
    }


}
