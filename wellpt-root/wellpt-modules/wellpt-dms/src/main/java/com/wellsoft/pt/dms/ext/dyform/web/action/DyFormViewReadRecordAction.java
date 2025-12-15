/*
 * @(#)Feb 16, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.dyform.web.action;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.common.marker.entity.ReadMarker;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.dms.config.support.Configuration;
import com.wellsoft.pt.dms.config.support.DmsFileManagerConfiguration;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.support.FileManagerDyFormActionData;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.ext.dyform.support.ReadRecords;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.comparator.IdEntityComparators;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 16, 2017.1	zhulh		Feb 16, 2017		Create
 * </pre>
 * @date Feb 16, 2017
 */
@Action("表单单据操作")
public class DyFormViewReadRecordAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6185570346483889901L;

    @Autowired
    private ReadMarkerService readMarkerService;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    /**
     * @return
     */
    @ActionConfig(name = "查看阅读记录", id = DyFormActions.ACTION_VIEW_READ_RECORD, executeJsModule = "DmsDyformViewReadRecordAction")
    @ResponseBody
    public ResultMessage actionPerformed(@RequestBody FileManagerDyFormActionData dyFormActionData) {
        // 从文件库查看阅读记录
        String folderUuid = dyFormActionData.getFolderUuid();
        Configuration configuration = getActionContext().getConfiguration();
        if (StringUtils.isNotBlank(folderUuid) && configuration instanceof DmsFileManagerConfiguration) {
            ((DmsFileManagerConfiguration) configuration).loadStore(folderUuid);
        }

        // 启用阅读记录
        boolean isEnableReadRecord = configuration.isEnableReadRecord();
        if (Boolean.FALSE.equals(isEnableReadRecord)) {
            throw new RuntimeException("数据管理未启用阅读记录，不能进行查看阅读记录操作!");
        }

        // 阅读人员字段
        String readRecordField = configuration.getReadRecordField();
        if (StringUtils.isBlank(readRecordField)) {
            throw new RuntimeException("数据管理阅读人员字段未配置，不能查看阅读记录!");
        }

        ReadRecords readRecords = new ReadRecords();
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setData(readRecords);

        DyFormData dyFormData = dyFormActionData.getDyFormData();
        dyFormData = dyFormApiFacade.getDyFormData(dyFormData.getFormUuid(), dyFormData.getDataUuid());
        Object fieldValue = dyFormData.getFieldValue(readRecordField);

        // 阅读人员字段为空值，直接返回
        if (fieldValue == null) {
            return resultMessage;
        }

        if (fieldValue != null) {
            // long time1 = System.currentTimeMillis();
            List<String> readUserIds = new ArrayList<String>();
            List<ReadMarker> tmpReadMarkers = readMarkerService.getReadMarkers(dyFormData.getDataUuid());
            List<ReadMarker> readMarkers = BeanUtils.convertCollection(tmpReadMarkers, ReadMarker.class);
            // 按创建时间倒序
            Collections.sort(readMarkers, IdEntityComparators.CREATE_TIME_DESC);
            for (ReadMarker marker : readMarkers) {
                readUserIds.add(marker.getUserId());
            }

            // long time2 = System.currentTimeMillis();
            // long time1T2 = time2 - time1;

            // 所有人员
            Map<String, String> allUserMap = orgApiFacade.getUsersByOrgIds(fieldValue.toString());
            // long time3 = System.currentTimeMillis();
            // long time2T3 = time3 - time2;
            // 已阅人员
            Map<String, String> readUserMap = null;
            if (!readUserIds.isEmpty()) {
                List<MultiOrgUserAccount> readUserList = orgApiFacade.queryUserAccountListByIds(readUserIds);
                if (!readUserList.isEmpty()) {
                    readUserMap = getReadUserMap(readUserList);
                }
            }

            // long time4 = System.currentTimeMillis();
            // long time3T4 = time4 - time3;

            List<ReadRecords.ReadRecord> records = new ArrayList<ReadRecords.ReadRecord>();
            readRecords.setRecords(records);
            for (ReadMarker marker : readMarkers) {
                // 排除不在阅读字段的人
                if (!allUserMap.containsKey(marker.getUserId())) {
                    continue;
                }
                ReadRecords.ReadRecord record = new ReadRecords.ReadRecord();
                record.setUserId(marker.getUserId());
                record.setUserName(readUserMap.get(marker.getUserId()));
                record.setReadTime(DateUtils.formatDateTimeMin(marker.getCreateTime()));
                records.add(record);
            }

            // long time5 = System.currentTimeMillis();
            // long time4T5 = time5 - time4;

            List<String> allUserNames = getReadUserNames(allUserMap);
            if (null != readUserMap && !readUserMap.isEmpty()) {
                List<String> readUserNames = getReadUserNames(readUserMap);
                allUserNames.removeAll(readUserNames);
            }
            readRecords.setUnreadUserName(StringUtils.join(allUserNames, Separator.SEMICOLON.getValue()));

            // long time6 = System.currentTimeMillis();
            // long time1T6 = time6 - time1;
            // StringBuffer logmsg = new StringBuffer();
            // logmsg.append("获取已阅记录表数据耗时：" + time1T2 + "ms,");
            // logmsg.append("获取发布对象人员数据耗时：" + time2T3 + "ms,");
            // logmsg.append("获取已阅人员数据耗时：" + time3T4 + "ms,");
            // logmsg.append("排除不再阅读字段人员耗时：" + time4T5 + "ms,");
            // logmsg.append("查看阅读记录总耗时：" + time1T6 + "ms.");
            // logger.error("查看阅读记录:" + logmsg.toString());
        }

        return resultMessage;
    }

    /**
     * @param userMap
     * @return
     */
    private List<String> getReadUserNames(Map<String, String> userMap) {
        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(userMap.values().toArray(new String[0])));
        return list;
    }

    /**
     * @param userList
     * @return
     */
    private Map<String, String> getReadUserMap(List<MultiOrgUserAccount> userList) {
        Map<String, String> map = new HashMap<String, String>();
        for (MultiOrgUserAccount user : userList) {
            map.put(user.getId(), user.getUserName());
        }
        return map;
    }

}
