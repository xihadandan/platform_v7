package com.wellsoft.pt.repository.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.repository.dao.FileWaitUploadDao;
import com.wellsoft.pt.repository.entity.FileWaitUploadEntity;
import com.wellsoft.pt.repository.service.FileWaitUploadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

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
@Service
public class FileWaitUploadServiceImpl extends AbstractJpaServiceImpl<FileWaitUploadEntity, FileWaitUploadDao, String> implements FileWaitUploadService {
    @Override
    @Transactional
    public boolean lock(String uuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        return this.dao.updateByNamedHQL("updateFileWaitUploadLock", params) > 0;
    }

    @Override
    @Transactional
    public void saveFailLog(String uuid, String msg, boolean fail, Date retryTime) {
        FileWaitUploadEntity fileWaitUploadEntity = this.getOne(uuid);
        if (fail) {
            fileWaitUploadEntity.setFailCount(fileWaitUploadEntity.getFailCount() + 1);
        }
        if (retryTime != null) {
            fileWaitUploadEntity.setRetryTime(retryTime);
        }
        fileWaitUploadEntity.setLog(msg);
        fileWaitUploadEntity.setDataLock(0);
        this.save(fileWaitUploadEntity);
    }
}
