package com.wellsoft.pt.repository.dao;

import com.wellsoft.pt.repository.entity.FolderOperateLog;

import java.util.Date;
import java.util.List;

public interface FolderOperateLogDao {
    List<FolderOperateLog> getLogsAfterTime(String folderID, Date date);

    public Date getLastModifyTimeOfFolder(String folderID);

    FolderOperateLog getLogByUuid(String uuid);
}
