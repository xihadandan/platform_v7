package com.wellsoft.pt.repository.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.repository.dao.FileWaitUploadDao;
import com.wellsoft.pt.repository.entity.FileWaitUploadEntity;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年11月04日   chenq	 Create
 * </pre>
 */
public interface FileWaitUploadService extends JpaService<FileWaitUploadEntity, FileWaitUploadDao, String> {
    boolean lock(String uuid);

    void saveFailLog(String uuid, String msg, boolean fail, Date retryTime);
}
