package com.wellsoft.pt.ei.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dto.ImportEntity;
import com.wellsoft.pt.ei.dto.mail.ContactGroupData;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;
import com.wellsoft.pt.webmail.constant.MailConstant;
import com.wellsoft.pt.webmail.entity.WmMailContactBookGroupEntity;
import com.wellsoft.pt.webmail.service.WmMailContactBookGrpService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yt
 * @Date: 2021/9/28 19:04
 * @Description:
 */
@Service
@Transactional(readOnly = true)
public class MailContactGroupExpImpServiceImpl extends AbstractMailExportService<ContactGroupData, WmMailContactBookGroupEntity> {

    @Autowired
    private WmMailContactBookGrpService wmMailContactBookGrpService;

    @Override
    public int order() {
        return 4;
    }

    @Override
    public Class dataClass() {
        return ContactGroupData.class;
    }

    @Override
    public String fileName() {
        return "邮件_联系人分组";
    }

    @Override
    public String dataChildType() {
        return DataExportConstants.DATA_TYPE_MAIL_CONTACT_GROUP;
    }

    @Override
    public long total(String systemUnitId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("systemUnitId", systemUnitId);
        long count = wmMailContactBookGrpService.getDao().countByHQL("select count(uuid) from WmMailContactBookGroupEntity where systemUnitId=:systemUnitId", params);
        return count;
    }

    @Override
    public List<WmMailContactBookGroupEntity> queryAll(String systemUnitId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("systemUnitId", systemUnitId);
        List<WmMailContactBookGroupEntity> list = wmMailContactBookGrpService.listByHQL("from WmMailContactBookGroupEntity where systemUnitId=:systemUnitId", params);
        return list;
    }

    @Override
    public List<WmMailContactBookGroupEntity> queryByPage(String systemUnitId, Integer currentPage, Integer pageSize) {
        PagingInfo pagingInfo = new PagingInfo(currentPage, pageSize);
        Map<String, Object> params = Maps.newHashMap();
        params.put("systemUnitId", systemUnitId);
        List<WmMailContactBookGroupEntity> list = wmMailContactBookGrpService.listByHQLAndPage("from WmMailContactBookGroupEntity where systemUnitId=:systemUnitId", params, pagingInfo);
        return list;
    }

    @Override
    public ContactGroupData toData(WmMailContactBookGroupEntity contactBookGroupEntity) {
        ContactGroupData groupData = new ContactGroupData();
        groupData.setGroupId(contactBookGroupEntity.getGroupId());
        groupData.setGroupName(contactBookGroupEntity.getGroupName());
        groupData.setUserId(contactBookGroupEntity.getCreator());
        groupData.setUuid(contactBookGroupEntity.getUuid());
        return groupData;
    }

    @Override
    public String getUuid(WmMailContactBookGroupEntity wmMailContactBookGroupEntity) {
        return wmMailContactBookGroupEntity.getUuid();
    }

    @Override
    public String getDataUuid(ContactGroupData contactGroupData) {
        return contactGroupData.getUuid();
    }

    @Override
    public String getId(WmMailContactBookGroupEntity wmMailContactBookGroupEntity) {
        return wmMailContactBookGroupEntity.getGroupId();
    }

    @Override
    public String getDataId(ContactGroupData contactGroupData) {
        return contactGroupData.getGroupId();
    }

    @Override
    @Transactional
    public ImportEntity<WmMailContactBookGroupEntity, ContactGroupData> save(ContactGroupData contactGroupData, String systemUnitId, boolean replace, Map<String, String> dependentDataMap) {
        WmMailContactBookGroupEntity groupEntity = null;
        if (replace) {
            DataImportTaskLog taskLog = taskLogService.getBySourceUuid(contactGroupData.getUuid());
            if (taskLog != null && taskLog.getAfterImportUuid() != null) {
                groupEntity = wmMailContactBookGrpService.getOne(taskLog.getAfterImportUuid());
            }
        }
        if (groupEntity == null) {
            groupEntity = new WmMailContactBookGroupEntity();
        }
        groupEntity.setSystemUnitId(systemUnitId);
        groupEntity.setCreator(super.getAfterImportId(contactGroupData.getUserId()));
        groupEntity.setCreateTime(new Date());
        groupEntity.setGroupId(MailConstant.CONTACT_BOOK_GRP_ID_PREFIX + DateFormatUtils.format(new Date(), "yyMMddHHmmssSSS"));
        groupEntity.setGroupName(contactGroupData.getGroupName());

        wmMailContactBookGrpService.update(groupEntity);

        ImportEntity<WmMailContactBookGroupEntity, ContactGroupData> importEntity = new ImportEntity<>();
        importEntity.setObj(groupEntity);
        importEntity.setSorce(contactGroupData);
        dependentDataMap.put(contactGroupData.getGroupId(), groupEntity.getGroupId());
        dependentDataMap.put(contactGroupData.getUuid(), groupEntity.getUuid());
        return importEntity;

    }


}
