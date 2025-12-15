package com.wellsoft.pt.ei.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dto.ImportEntity;
import com.wellsoft.pt.ei.dto.mail.MailFolderData;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;
import com.wellsoft.pt.webmail.entity.WmMailFolderEntity;
import com.wellsoft.pt.webmail.service.WmMailFolderService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yt
 * @Date: 2021/9/27 18:59
 * @Description:
 */
@Service
@Transactional(readOnly = true)
public class MailFolderExpImpServiceImpl extends AbstractMailExportService<MailFolderData, WmMailFolderEntity> {


    @Autowired
    private WmMailFolderService wmMailFolderService;

    @Override
    public int order() {
        return 1;
    }

    @Override
    public Class dataClass() {
        return MailFolderData.class;
    }

    @Override
    public String fileName() {
        return "邮件_文件夹";
    }

    @Override
    public String dataChildType() {
        return DataExportConstants.DATA_TYPE_MAIL_FOLDER;
    }

    @Override
    public long total(String systemUnitId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("systemUnitId", systemUnitId);
        long count = wmMailFolderService.getDao().countByHQL("select count(uuid) from WmMailFolderEntity where systemUnitId=:systemUnitId", params);
        return count;
    }

    @Override
    public List<WmMailFolderEntity> queryAll(String systemUnitId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("systemUnitId", systemUnitId);
        List<WmMailFolderEntity> list = wmMailFolderService.listByHQL("from WmMailFolderEntity where systemUnitId=:systemUnitId", params);
        return list;
    }

    @Override
    public List<WmMailFolderEntity> queryByPage(String systemUnitId, Integer currentPage, Integer pageSize) {
        PagingInfo pagingInfo = new PagingInfo(currentPage, pageSize);
        Map<String, Object> params = Maps.newHashMap();
        params.put("systemUnitId", systemUnitId);
        List<WmMailFolderEntity> list = wmMailFolderService.listByHQLAndPage("from WmMailFolderEntity where systemUnitId=:systemUnitId", params, pagingInfo);
        return list;
    }

    @Override
    public MailFolderData toData(WmMailFolderEntity wmMailFolderEntity) {
        MailFolderData folderData = new MailFolderData();
        folderData.setUuid(wmMailFolderEntity.getUuid());
        folderData.setUserId(wmMailFolderEntity.getUserId());
        folderData.setFolderCode(wmMailFolderEntity.getFolderCode());
        folderData.setFolderName(wmMailFolderEntity.getFolderName());
        folderData.setSeq(wmMailFolderEntity.getSeq());
        return folderData;
    }

    @Override
    public String getUuid(WmMailFolderEntity wmMailFolderEntity) {
        return wmMailFolderEntity.getUuid();
    }

    @Override
    public String getDataUuid(MailFolderData mailFolderData) {
        return mailFolderData.getUuid();
    }

    @Override
    public String getId(WmMailFolderEntity wmMailFolderEntity) {
        return wmMailFolderEntity.getFolderCode();
    }

    @Override
    public String getDataId(MailFolderData mailFolderData) {
        return mailFolderData.getFolderCode();
    }

    @Override
    @Transactional
    public ImportEntity<WmMailFolderEntity, MailFolderData> save(MailFolderData mailFolderData, String systemUnitId, boolean replace, Map<String, String> dependentDataMap) {
        WmMailFolderEntity folderEntity = null;
        if (replace) {
            DataImportTaskLog taskLog = taskLogService.getBySourceUuid(mailFolderData.getUuid());
            if (taskLog != null && taskLog.getAfterImportUuid() != null) {
                folderEntity = wmMailFolderService.getOne(taskLog.getAfterImportUuid());
            }
        }
        if (folderEntity == null) {
            folderEntity = new WmMailFolderEntity();
        }
        folderEntity.setUserId(super.getAfterImportId(mailFolderData.getUserId()));
        folderEntity.setFolderName(mailFolderData.getFolderName());
        if (CollectionUtils.isNotEmpty(wmMailFolderService.listByEntity(folderEntity))) {
            throw new RuntimeException("已存在同名的文件夹");
        }
        folderEntity.setSystemUnitId(systemUnitId);
        folderEntity.setSeq(mailFolderData.getSeq());
        folderEntity.setFolderCode(
                String.format("%s_%s_%s", WmMailFolderEntity.FOLDER_CODE_PREFIX, folderEntity.getUserId(),
                        DateFormatUtils.format(Calendar.getInstance(), "yyyyMMddHHmmss")));
        wmMailFolderService.save(folderEntity);
        ImportEntity<WmMailFolderEntity, MailFolderData> importEntity = new ImportEntity<>();
        importEntity.setObj(folderEntity);
        importEntity.setSorce(mailFolderData);
        dependentDataMap.put(mailFolderData.getFolderCode(), folderEntity.getFolderCode());
        return importEntity;


    }


}
