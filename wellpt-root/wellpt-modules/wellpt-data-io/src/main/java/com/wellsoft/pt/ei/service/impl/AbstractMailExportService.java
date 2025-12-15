package com.wellsoft.pt.ei.service.impl;

import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;
import com.wellsoft.pt.ei.service.DataImportTaskLogService;
import com.wellsoft.pt.ei.service.ExpImpService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Auther: yt
 * @Date: 2021/9/24 11:34
 * @Description:
 */
public abstract class AbstractMailExportService<D, T> implements ExpImpService<D, T> {

    @Autowired
    protected DataImportTaskLogService taskLogService;

    @Override
    public String filePath() {
        return DataExportConstants.DATA_TYPE_MAIL;
    }

    @Override
    public String getId(T t) {
        return null;
    }

    @Override
    public String getDataId(D d) {
        return null;
    }


    protected String getAfterImportId(String sourceId) {
        DataImportTaskLog taskLog = taskLogService.getBySourceId(sourceId);
        if (taskLog == null) {
            throw new RuntimeException("数据依赖缺失");
        }
        return taskLog.getAfterImportId();
    }

    protected String getAfterImportUuid(String sourceUuid) {
        DataImportTaskLog taskLog = taskLogService.getBySourceUuid(sourceUuid);
        if (taskLog == null) {
            throw new RuntimeException("数据依赖缺失");
        }
        return taskLog.getAfterImportUuid();
    }

}
