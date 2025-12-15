package com.wellsoft.pt.ei.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dto.ImportEntity;
import com.wellsoft.pt.ei.dto.mail.ContactData;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;
import com.wellsoft.pt.webmail.constant.MailConstant;
import com.wellsoft.pt.webmail.entity.WmMailContactBookEntity;
import com.wellsoft.pt.webmail.service.WmMailContactBookService;
import org.apache.commons.lang.StringUtils;
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
public class MailContactExpImpServiceImpl extends AbstractMailExportService<ContactData, WmMailContactBookEntity> {

    @Autowired
    private WmMailContactBookService wmMailContactBookService;

    @Override
    public int order() {
        return 5;
    }

    @Override
    public Class dataClass() {
        return ContactData.class;
    }

    @Override
    public String fileName() {
        return "邮件_联系人";
    }

    @Override
    public String dataChildType() {
        return DataExportConstants.DATA_TYPE_MAIL_CONTACT;
    }

    @Override
    public long total(String systemUnitId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("systemUnitId", systemUnitId);
        long count = wmMailContactBookService.getDao().countByHQL("select count(uuid) from WmMailContactBookEntity where systemUnitId=:systemUnitId", params);
        return count;
    }

    @Override
    public List<WmMailContactBookEntity> queryAll(String systemUnitId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("systemUnitId", systemUnitId);
        List<WmMailContactBookEntity> list = wmMailContactBookService.listByHQL("from WmMailContactBookEntity where systemUnitId=:systemUnitId", params);
        return list;
    }

    @Override
    public List<WmMailContactBookEntity> queryByPage(String systemUnitId, Integer currentPage, Integer pageSize) {
        PagingInfo pagingInfo = new PagingInfo(currentPage, pageSize);
        Map<String, Object> params = Maps.newHashMap();
        params.put("systemUnitId", systemUnitId);
        List<WmMailContactBookEntity> list = wmMailContactBookService.listByHQLAndPage("from WmMailContactBookEntity where systemUnitId=:systemUnitId", params, pagingInfo);
        return list;
    }

    @Override
    public ContactData toData(WmMailContactBookEntity wmMailContactBookEntity) {
        ContactData contactData = new ContactData();
        contactData.setUuid(wmMailContactBookEntity.getUuid());
        contactData.setUserId(wmMailContactBookEntity.getCreator());
        contactData.setContactId(wmMailContactBookEntity.getContactId());
        contactData.setContactName(wmMailContactBookEntity.getContactName());
        contactData.setPersonalEmail(wmMailContactBookEntity.getPersonalEmail());
        contactData.setCellphoneNumber(wmMailContactBookEntity.getCellphoneNumber());
        contactData.setRemark(wmMailContactBookEntity.getRemark());
        contactData.setGroupUuid(wmMailContactBookEntity.getGroupUuid());
        return contactData;
    }

    @Override
    public String getUuid(WmMailContactBookEntity wmMailContactBookEntity) {
        return wmMailContactBookEntity.getUuid();
    }

    @Override
    public String getDataUuid(ContactData contactData) {
        return contactData.getUuid();
    }

    @Override
    public String getId(WmMailContactBookEntity wmMailContactBookEntity) {
        return wmMailContactBookEntity.getContactId();
    }

    @Override
    public String getDataId(ContactData contactData) {
        return contactData.getContactId();
    }

    @Override
    @Transactional
    public ImportEntity<WmMailContactBookEntity, ContactData> save(ContactData contactData, String systemUnitId, boolean replace, Map<String, String> dependentDataMap) {
        WmMailContactBookEntity bookEntity = null;
        if (replace) {
            DataImportTaskLog taskLog = taskLogService.getBySourceUuid(contactData.getUuid());
            if (taskLog != null && taskLog.getAfterImportUuid() != null) {
                bookEntity = wmMailContactBookService.getOne(taskLog.getAfterImportUuid());
            }
        }
        if (bookEntity == null) {
            bookEntity = new WmMailContactBookEntity();
        }

        String contactId = MailConstant.CONTACT_BOOK_ID_PREFIX + DateFormatUtils.format(new Date(), "yyMMddHHmmssSSS");
        bookEntity.setCreator(super.getAfterImportId(contactData.getUserId()));
        bookEntity.setCreateTime(new Date());
        bookEntity.setContactName(contactData.getContactName());
        bookEntity.setPersonalEmail(contactData.getPersonalEmail());
        bookEntity.setCellphoneNumber(contactData.getCellphoneNumber());
        bookEntity.setRemark(contactData.getRemark());
        bookEntity.setContactId(contactId);

        String groupUuid = dependentDataMap.get(contactData.getGroupUuid());
        if (StringUtils.isBlank(groupUuid)) {
            DataImportTaskLog taskLog = taskLogService.getBySourceUuid(contactData.getGroupUuid());
            if (taskLog != null) {
                groupUuid = taskLog.getAfterImportUuid();
            }
        }
        ImportEntity<WmMailContactBookEntity, ContactData> importEntity = new ImportEntity<>();
        if (StringUtils.isNotBlank(groupUuid)) {
            bookEntity.setGroupUuid(groupUuid);
            wmMailContactBookService.update(bookEntity);
            dependentDataMap.put(contactData.getContactId(), bookEntity.getContactId());
        } else {
            importEntity.setPostProcess(true);
        }
        importEntity.setObj(bookEntity);
        importEntity.setSorce(contactData);
        return importEntity;
    }


    @Override
    @Transactional
    public void update(WmMailContactBookEntity wmMailContactBookEntity, ContactData contactData, Map<String, String> dependentDataMap) {
        wmMailContactBookEntity.setGroupUuid(this.getGroupUuid(contactData, dependentDataMap));
        wmMailContactBookService.update(wmMailContactBookEntity);
    }

    private String getGroupUuid(ContactData contactData, Map<String, String> dependentDataMap) {
        DataImportTaskLog taskLog = taskLogService.getBySourceUuid(contactData.getGroupUuid());
        if (taskLog != null) {
            return taskLog.getAfterImportUuid();
        } else {
            return dependentDataMap.get(contactData.getGroupUuid());
        }
    }
}
