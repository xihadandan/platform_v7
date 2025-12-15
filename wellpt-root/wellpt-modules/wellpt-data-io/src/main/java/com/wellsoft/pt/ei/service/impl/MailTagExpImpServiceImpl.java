package com.wellsoft.pt.ei.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dto.ImportEntity;
import com.wellsoft.pt.ei.dto.mail.MailTag;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;
import com.wellsoft.pt.webmail.entity.WmMailTagEntity;
import com.wellsoft.pt.webmail.service.WmMailTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Auther: yt
 * @Date: 2021/9/28 19:04
 * @Description:
 */
@Service
@Transactional(readOnly = true)
public class MailTagExpImpServiceImpl extends AbstractMailExportService<MailTag, WmMailTagEntity> {

    @Autowired
    private WmMailTagService wmMailTagService;

    @Override
    public int order() {
        return 2;
    }

    @Override
    public Class dataClass() {
        return MailTag.class;
    }

    @Override
    public String fileName() {
        return "邮件_自定义标签";
    }

    @Override
    public String dataChildType() {
        return DataExportConstants.DATA_TYPE_MAIL_TAG;
    }

    @Override
    public long total(String systemUnitId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("systemUnitId", systemUnitId);
        long count = wmMailTagService.getDao().countByHQL("select count(uuid) from WmMailTagEntity where systemUnitId=:systemUnitId", params);
        return count;
    }

    @Override
    public List<WmMailTagEntity> queryAll(String systemUnitId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("systemUnitId", systemUnitId);
        List<WmMailTagEntity> list = wmMailTagService.listByHQL("from WmMailTagEntity where systemUnitId=:systemUnitId", params);
        return list;
    }

    @Override
    public List<WmMailTagEntity> queryByPage(String systemUnitId, Integer currentPage, Integer pageSize) {
        PagingInfo pagingInfo = new PagingInfo(currentPage, pageSize);
        Map<String, Object> params = Maps.newHashMap();
        params.put("systemUnitId", systemUnitId);
        List<WmMailTagEntity> list = wmMailTagService.listByHQLAndPage("from WmMailTagEntity where systemUnitId=:systemUnitId", params, pagingInfo);
        return list;
    }

    @Override
    public MailTag toData(WmMailTagEntity wmMailTagEntity) {
        MailTag mailTag = new MailTag();
        mailTag.setUuid(wmMailTagEntity.getUuid());
        mailTag.setUserId(wmMailTagEntity.getUserId());
        mailTag.setTagName(wmMailTagEntity.getTagName());
        mailTag.setTagColor(wmMailTagEntity.getTagColor());
        mailTag.setSeq(wmMailTagEntity.getSeq());
        return mailTag;
    }

    @Override
    public String getUuid(WmMailTagEntity wmMailTagEntity) {
        return wmMailTagEntity.getUuid();
    }

    @Override
    public String getDataUuid(MailTag mailTag) {
        return mailTag.getUuid();
    }

    @Override
    @Transactional
    public ImportEntity<WmMailTagEntity, MailTag> save(MailTag mailTag, String systemUnitId, boolean replace, Map<String, String> dependentDataMap) {
        WmMailTagEntity tagEntity = null;
        if (replace) {
            DataImportTaskLog taskLog = taskLogService.getBySourceUuid(mailTag.getUuid());
            if (taskLog != null && taskLog.getAfterImportUuid() != null) {
                tagEntity = wmMailTagService.getOne(taskLog.getAfterImportUuid());
            }
        }
        if (tagEntity == null) {
            tagEntity = new WmMailTagEntity();
        }
        tagEntity.setUserId(super.getAfterImportId(mailTag.getUserId()));
        tagEntity.setSystemUnitId(systemUnitId);
        tagEntity.setTagName(mailTag.getTagName());
        tagEntity.setTagColor(mailTag.getTagColor());
        tagEntity.setSeq(mailTag.getSeq());
        wmMailTagService.save(tagEntity);
        ImportEntity<WmMailTagEntity, MailTag> importEntity = new ImportEntity<>();
        importEntity.setObj(tagEntity);
        importEntity.setSorce(mailTag);
        return importEntity;
    }

}
