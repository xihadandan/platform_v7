/*
 * @(#)2015-10-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query.api.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.bpm.engine.query.api.TaskQuery;
import com.wellsoft.pt.bpm.engine.query.api.TaskQueryItem;
import com.wellsoft.pt.jpa.query.AbstractQuery;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-10-19.1	zhulh		2015-10-19		Create
 * </pre>
 * @date 2015-10-19
 */
@Service
@Scope(value = "prototype")
public class TaskQueryImpl extends AbstractQuery<TaskQuery, TaskQueryItem> implements TaskQuery {

    @Autowired
    AclTaskService aclTaskService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#count()
     */
    @Override
    @Transactional(readOnly = true)
    public long count() {
        return this.nativeDao.countByNamedQuery("countTaskQuery", values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#uniqueResult()
     */
    @Override
    @Transactional(readOnly = true)
    public TaskQueryItem uniqueResult() {
        return this.nativeDao.findUniqueByNamedQuery("listTaskQuery", values, TaskQueryItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#list()
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaskQueryItem> list() {
        return list(TaskQueryItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#list(java.lang.Class)
     */
    @Override
    @Transactional(readOnly = true)
    public <ITEM extends Serializable> List<ITEM> list(Class<ITEM> itemClass) {
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setPageSize(maxResults);
        pagingInfo.setFirst(firstResult);
        pagingInfo.setAutoCount(false);
        // 工作数据按计时状态分组取总数
        if (Boolean.TRUE.equals(this.values.get("groupAndCountByTimingState"))) {
            return this.nativeDao.namedQuery("groupAndCountTaskByTimingStateQuery", values, itemClass, pagingInfo);
        } else if (Boolean.TRUE.equals(this.values.get("groupAndCountByFlowDefId"))) {
            // 工作数据按流程定义ID分组取总数
            return this.nativeDao.namedQuery("groupAndCountTaskByFlowDefIdQuery", values, itemClass, pagingInfo);
        }
        return this.nativeDao.namedQuery("listTaskQuery", values, itemClass, pagingInfo);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#flowDefId(java.lang.String)
     */
    @Override
    public TaskQuery flowDefId(String flowDefId) {
        return addParameter("flowDefId", flowDefId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#flowDefIdLike(java.lang.String)
     */
    @Override
    public TaskQuery flowDefIdLike(String flowDefId) {
        return addParameter("flowDefIdLike", flowDefId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#addFlowDefId(java.lang.String)
     */
    @Override
    public TaskQuery addFlowDefId(String flowDefId) {
        return addParameterList("flowDefIds", flowDefId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#flowDefUuid(java.lang.String)
     */
    @Override
    public TaskQuery flowDefUuid(String flowDefUuid) {
        return addParameter("flowDefUuid", flowDefUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#addFlowDefUuid(java.lang.String)
     */
    @Override
    public TaskQuery addFlowDefUuid(String flowDefUuid) {
        return addParameterList("flowDefUuids", flowDefUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#flowName(java.lang.String)
     */
    @Override
    public TaskQuery flowName(String flowName) {
        return addParameter("flowName", flowName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#flowNameLike(java.lang.String)
     */
    @Override
    public TaskQuery flowNameLike(String flowName) {
        return addParameter("flowNameLike", flowName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#flowCategory(java.lang.String)
     */
    @Override
    public TaskQuery flowCategory(String flowCategory) {
        return addParameter("flowCategory", flowCategory);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#flowInstUuid(java.lang.String)
     */
    @Override
    public TaskQuery flowInstUuid(String flowInstUuid) {
        return addParameter("flowInstUuid", flowInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#title(java.lang.String)
     */
    @Override
    public TaskQuery title(String title) {
        return addParameter("title", title);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#titleLike(java.lang.String)
     */
    @Override
    public TaskQuery titleLike(String title) {
        return addParameter("titleLike", title);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#flowStartUserId(java.lang.String)
     */
    @Override
    public TaskQuery flowStartUserId(String flowStartUserId) {
        return addParameter("flowStartUserId", flowStartUserId);
    }

    @Override
    public TaskQuery flowStartUserNameLike(String flowStartUserName) {
        return addParameter("flowStartUserNameLike", flowStartUserName);
    }

    @Override
    public TaskQuery flowStartDepartmentId(String flowStartDepartmentId) {
        return addParameter("flowStartDepartmentId", flowStartDepartmentId);
    }

    @Override
    public TaskQuery flowStartDepartmentNameLike(String flowStartDepartmentName) {
        return addParameter("flowStartDepartmentNameLike", flowStartDepartmentName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#flowStartTime(java.util.Date)
     */
    @Override
    public TaskQuery flowStartTime(Date flowStartTime) {
        return addParameter("flowStartTime", flowStartTime);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#flowEndTime(java.util.Date)
     */
    @Override
    public TaskQuery flowEndTime(Date flowEndTime) {
        return addParameter("flowEndTime", flowEndTime);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#flowEndTimeIsNull()
     */
    @Override
    public TaskQuery flowEndTimeIsNull() {
        return addParameter("flowEndTimeIsNull", "t2.end_time is null");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#flowEndTimeIsNotNull()
     */
    @Override
    public TaskQuery flowEndTimeIsNotNull() {
        return addParameter("flowEndTimeIsNotNull", "t2.end_time is not null");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#isActive(java.lang.Boolean)
     */
    @Override
    public TaskQuery isActive(Boolean isActive) {
        return addParameter("isActive", isActive);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#taskName(java.lang.String)
     */
    @Override
    public TaskQuery taskName(String taskName) {
        return addParameter("taskName", taskName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#taskNameLike(java.lang.String)
     */
    @Override
    public TaskQuery taskNameLike(String taskName) {
        return addParameter("taskNameLike", taskName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#taskId(java.lang.String)
     */
    @Override
    public TaskQuery taskId(String taskId) {
        return addParameter("taskId", taskId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#taskIdLike(java.lang.String)
     */
    @Override
    public TaskQuery taskIdLike(String taskId) {
        return addParameter("taskIdLike", taskId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#addTaskId(java.lang.String)
     */
    @Override
    public TaskQuery addTaskId(String taskId) {
        return addParameterList("taskIds", taskId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#formUuid(java.lang.String)
     */
    @Override
    public TaskQuery formUuid(String formUuid) {
        return addParameter("formUuid", formUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#dataUuid(java.lang.String)
     */
    @Override
    public TaskQuery dataUuid(String dataUuid) {
        return addParameter("dataUuid", dataUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#serialNo(java.lang.String)
     */
    @Override
    public TaskQuery serialNo(String serialNo) {
        return addParameter("serialNo", serialNo);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#serialNoLike(java.lang.String)
     */
    @Override
    public TaskQuery serialNoLike(String serialNo) {
        return addParameter("serialNoLike", serialNo);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#owner(java.lang.String)
     */
    @Override
    public TaskQuery owner(String owner) {
        return addParameter("owner", owner);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#preOperatorId(java.lang.String)
     */
    @Override
    public TaskQuery preOperatorId(String preOperatorId) {
        return addParameter("preOperatorId", preOperatorId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#preOperatorName(java.lang.String)
     */
    @Override
    public TaskQuery preOperatorName(String preOperatorName) {
        return addParameter("preOperatorName", preOperatorName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#preOperatorNameLike(java.lang.String)
     */
    @Override
    public TaskQuery preOperatorNameLike(String preOperatorName) {
        return addParameter("preOperatorNameLike", preOperatorName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#todoUserId(java.lang.String)
     */
    @Override
    public TaskQuery todoUserId(String todoUserId) {
        return addParameter("todoUserId", todoUserId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#todoUserLike(java.lang.String)
     */
    @Override
    public TaskQuery todoUserLike(String todoUserId) {
        return addParameter("todoUserLike", todoUserId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#todoUserName(java.lang.String)
     */
    @Override
    public TaskQuery todoUserName(String todoUserName) {
        return addParameter("todoUserName", todoUserName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#todoUserNameLike(java.lang.String)
     */
    @Override
    public TaskQuery todoUserNameLike(String todoUserName) {
        return addParameter("todoUserNameLike", todoUserName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#action(java.util.Date)
     */
    @Override
    public TaskQuery action(Date action) {
        return addParameter("action", action);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#actionType(java.lang.String)
     */
    @Override
    public TaskQuery actionType(String actionType) {
        return addParameter("actionType", actionType);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#startTime(java.util.Date)
     */
    @Override
    public TaskQuery startTime(Date startTime) {
        return addParameter("startTime", startTime);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#endTime(java.util.Date)
     */
    @Override
    public TaskQuery endTime(Date endTime) {
        return addParameter("endTime", endTime);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#endTimeIsNull()
     */
    @Override
    public TaskQuery endTimeIsNull() {
        return addParameter("endTimeIsNull", "t1.end_time is null");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#alarmTime(java.util.Date)
     */
    @Override
    public TaskQuery alarmTime(Date alarmTime) {
        return addParameter("alarmTime", alarmTime);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#alarmTimeBefore(java.util.Date)
     */
    @Override
    public TaskQuery alarmTimeBefore(Date dueTime) {
        return addParameter("alarmTimeBefore", dueTime);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#alarmTimeAfter(java.util.Date)
     */
    @Override
    public TaskQuery alarmTimeAfter(Date dueTime) {
        return addParameter("alarmTimeAfter", dueTime);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#dueTime(java.util.Date)
     */
    @Override
    public TaskQuery dueTime(Date dueTime) {
        return addParameter("dueTime", dueTime);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#dueTimeBefore(java.util.Date)
     */
    @Override
    public TaskQuery dueTimeBefore(Date dueTime) {
        return addParameter("dueTimeBefore", dueTime);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#dueTimeAfter(java.util.Date)
     */
    @Override
    public TaskQuery dueTimeAfter(Date dueTime) {
        return addParameter("dueTimeAfter", dueTime);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#timingState(java.lang.Integer)
     */
    @Override
    public TaskQuery timingState(Integer timingState) {
        return addParameter("timingState", timingState);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#alarmState(java.lang.Integer)
     */
    @Override
    public TaskQuery alarmState(Integer alarmState) {
        return addParameter("alarmState", alarmState);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#overDueState(java.lang.Integer)
     */
    @Override
    public TaskQuery overDueState(Integer overDueState) {
        return addParameter("overDueState", overDueState);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#suspensionState(java.lang.Integer)
     */
    @Override
    public TaskQuery suspensionState(Integer suspensionState) {
        return addParameter("suspensionState", suspensionState);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText1(java.lang.String)
     */
    @Override
    public TaskQuery reservedText1(String reservedText1) {
        return addParameter("reservedText1", reservedText1);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText1Like(java.lang.String)
     */
    @Override
    public TaskQuery reservedText1Like(String flowDefId) {
        return addParameter("reservedText1Like", flowDefId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText2(java.lang.String)
     */
    @Override
    public TaskQuery reservedText2(String reservedText2) {
        return addParameter("reservedText2", reservedText2);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText2Like(java.lang.String)
     */
    @Override
    public TaskQuery reservedText2Like(String reservedText2) {
        return addParameter("reservedText2Like", reservedText2);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText3(java.lang.String)
     */
    @Override
    public TaskQuery reservedText3(String reservedText3) {
        return addParameter("reservedText3", reservedText3);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText3Like(java.lang.String)
     */
    @Override
    public TaskQuery reservedText3Like(String reservedText3) {
        return addParameter("reservedText3Like", reservedText3);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText4(java.lang.String)
     */
    @Override
    public TaskQuery reservedText4(String reservedText4) {
        return addParameter("reservedText4", reservedText4);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText4Like(java.lang.String)
     */
    @Override
    public TaskQuery reservedText4Like(String reservedText4) {
        return addParameter("reservedText4Like", reservedText4);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText5(java.lang.String)
     */
    @Override
    public TaskQuery reservedText5(String reservedText5) {
        return addParameter("reservedText5", reservedText5);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText5Like(java.lang.String)
     */
    @Override
    public TaskQuery reservedText5Like(String reservedText5) {
        return addParameter("reservedText5Like", reservedText5);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText6(java.lang.String)
     */
    @Override
    public TaskQuery reservedText6(String reservedText6) {
        return addParameter("reservedText6", reservedText6);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText6Like(java.lang.String)
     */
    @Override
    public TaskQuery reservedText6Like(String reservedText6) {
        return addParameter("reservedText6Like", reservedText6);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText7(java.lang.String)
     */
    @Override
    public TaskQuery reservedText7(String reservedText7) {
        return addParameter("reservedText7", reservedText7);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText7Like(java.lang.String)
     */
    @Override
    public TaskQuery reservedText7Like(String reservedText7) {
        return addParameter("reservedText7Like", reservedText7);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText8(java.lang.String)
     */
    @Override
    public TaskQuery reservedText8(String reservedText8) {
        return addParameter("reservedText8", reservedText8);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText8Like(java.lang.String)
     */
    @Override
    public TaskQuery reservedText8Like(String reservedText8) {
        return addParameter("reservedText8Like", reservedText8);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText9(java.lang.String)
     */
    @Override
    public TaskQuery reservedText9(String reservedText9) {
        return addParameter("reservedText9", reservedText9);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText9Like(java.lang.String)
     */
    @Override
    public TaskQuery reservedText9Like(String reservedText9) {
        return addParameter("reservedText9Like", reservedText9);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText10(java.lang.String)
     */
    @Override
    public TaskQuery reservedText10(String reservedText10) {
        return addParameter("reservedText10", reservedText10);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText10Like(java.lang.String)
     */
    @Override
    public TaskQuery reservedText10Like(String reservedText10) {
        return addParameter("reservedText10Like", reservedText10);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText11(java.lang.String)
     */
    @Override
    public TaskQuery reservedText11(String reservedText11) {
        return addParameter("reservedText11", reservedText11);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText11Like(java.lang.String)
     */
    @Override
    public TaskQuery reservedText11Like(String reservedText11) {
        return addParameter("reservedText11Like", reservedText11);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText12(java.lang.String)
     */
    @Override
    public TaskQuery reservedText12(String reservedText12) {
        return addParameter("reservedText12", reservedText12);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedText12Like(java.lang.String)
     */
    @Override
    public TaskQuery reservedText12Like(String reservedText12) {
        return addParameter("reservedText12Like", reservedText12);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedNumber1(java.lang.Integer)
     */
    @Override
    public TaskQuery reservedNumber1(Integer reservedNumber1) {
        return addParameter("reservedNumber1", reservedNumber1);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedNumber2(java.lang.Double)
     */
    @Override
    public TaskQuery reservedNumber2(Double reservedNumber2) {
        return addParameter("reservedNumber2", reservedNumber2);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedNumber3(java.lang.Double)
     */
    @Override
    public TaskQuery reservedNumber3(Double reservedNumber3) {
        return addParameter("reservedNumber3", reservedNumber3);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedDate1(java.util.Date)
     */
    @Override
    public TaskQuery reservedDate1(Date reservedDate1) {
        return addParameter("reservedDate1", reservedDate1);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#reservedDate2(java.util.Date)
     */
    @Override
    public TaskQuery reservedDate2(Date reservedDate2) {
        return addParameter("reservedDate2", reservedDate2);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#permission(java.lang.String, java.util.Collection)
     */
    @Override
    public TaskQuery permission(String userId, Collection<Permission> permissions) {
        List<String> userIds = new ArrayList<String>();
        userIds.add(userId);
        return permission(userIds, permissions);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#permission(java.util.List, java.util.Collection)
     */
    @Override
    public TaskQuery permission(List<String> userSids, Collection<Permission> permissions) {
        boolean widthPermission = permissions != null && !permissions.isEmpty();
        // 是否带权限查询
        if (widthPermission) {
            addParameter("widthPermission", permissions != null && !permissions.isEmpty());
            addParameter("userId", SpringSecurityUtils.getCurrentUserId());
            for (String sid : userSids) {
                addParameterList("userSids", sid);
            }
            String authWhere = aclTaskService.getAuthWhere(permissions);
            addParameter("authWhere", authWhere);
        }
        return this;
    }

    @Override
    public TaskQuery setLikeQueryConnector(String likeQueryConnector) {
        return addParameter("likeQueryConnector", likeQueryConnector);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#orderByStartTimeAsc()
     */
    @Override
    public TaskQuery orderByStartTimeAsc() {
        return addOrderBy("t1.start_time", "asc");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#orderByStartTimeDesc()
     */
    @Override
    public TaskQuery orderByStartTimeDesc() {
        return addOrderBy("t1.start_time", "desc");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#orderByAlarmStateAsc()
     */
    @Override
    public TaskQuery orderByAlarmStateAsc() {
        return addOrderBy("t1.alarm_state", "asc");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#orderByAlarmStateDesc()
     */
    @Override
    public TaskQuery orderByAlarmStateDesc() {
        return addOrderBy("t1.alarm_state", "desc");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#orderByOverDueStateAsc()
     */
    @Override
    public TaskQuery orderByOverDueStateAsc() {
        return addOrderBy("t1.over_due_state", "asc");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#orderByOverDueStateDesc()
     */
    @Override
    public TaskQuery orderByOverDueStateDesc() {
        return addOrderBy("t1.over_due_state", "desc");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#addFlowDefUuid(java.lang.String)
     */
    @Override
    public TaskQuery addReservedText8(String reservedText8) {
        return addParameterList("reservedText8s", reservedText8);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#addFlowDefUuid(java.lang.String)
     */
    @Override
    public TaskQuery addReservedText10(String reservedText10) {
        return addParameterList("reservedText10s", reservedText10);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#where(java.lang.String, java.util.Map)
     */
    @Override
    public TaskQuery where(String whereSql, Map<String, Object> params) {
        // 增加 suspensionState 不为3的判断（管理员删除 假删）
        if (!StringUtils.containsIgnoreCase(whereSql, "suspension_state") &&
                !this.values.containsKey("suspensionState")) {
            String adminDeleteFlagSql = " and t1.suspension_state in(0, 4) ";
            addParameter("defaultSuspensionState", true);
            addParameter("whereSql", whereSql + adminDeleteFlagSql);
        } else {
            addParameter("whereSql", whereSql);
        }
        for (Entry<String, Object> entry : params.entrySet()) {
            addParameter(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#order(java.lang.String)
     */
    @Override
    public TaskQuery order(String order) {
        addParameter("orderString", order);
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#join(java.lang.String)
     */
    @Override
    public TaskQuery join(String join) {
        addParameter("joinClause", join);
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskQuery#projection(java.lang.String)
     */
    @Override
    public TaskQuery projection(String projection) {
        addParameter("projectionClause", projection);
        return this;
    }

}
