/*
 * @(#)2015-10-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query.api;

import com.wellsoft.pt.jpa.query.Query;
import org.springframework.security.acls.model.Permission;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 环节实例查询接口
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
public interface TaskQuery extends Query<TaskQuery, TaskQueryItem> {

    TaskQuery flowDefId(String flowDefId);

    TaskQuery flowDefIdLike(String flowDefId);

    TaskQuery addFlowDefId(String flowDefId);

    TaskQuery flowDefUuid(String flowDefUuid);

    TaskQuery addFlowDefUuid(String flowDefUuid);

    TaskQuery flowName(String flowName);

    TaskQuery flowNameLike(String flowName);

    TaskQuery flowCategory(String flowCategory);

    TaskQuery flowInstUuid(String flowInstUuid);

    TaskQuery title(String title);

    TaskQuery titleLike(String title);

    TaskQuery flowStartUserId(String flowStartUserId);

    TaskQuery flowStartUserNameLike(String flowStartUserName);

    TaskQuery flowStartDepartmentId(String flowStartDepartmentId);

    TaskQuery flowStartDepartmentNameLike(String flowStartDepartmentName);

    TaskQuery flowStartTime(Date flowStartTime);

    TaskQuery flowEndTime(Date flowEndTime);

    TaskQuery flowEndTimeIsNull();

    TaskQuery flowEndTimeIsNotNull();

    TaskQuery isActive(Boolean isActive);

    TaskQuery taskName(String taskName);

    TaskQuery taskNameLike(String taskName);

    TaskQuery taskId(String taskId);

    TaskQuery taskIdLike(String taskId);

    TaskQuery addTaskId(String taskId);

    TaskQuery formUuid(String formUuid);

    TaskQuery dataUuid(String dataUuid);

    TaskQuery serialNo(String serialNo);

    TaskQuery serialNoLike(String serialNo);

    TaskQuery owner(String owner);

    TaskQuery preOperatorId(String preOperatorId);

    TaskQuery preOperatorName(String preOperatorName);

    TaskQuery preOperatorNameLike(String preOperatorName);

    TaskQuery todoUserId(String todoUserId);

    TaskQuery todoUserLike(String todoUserId);

    TaskQuery todoUserName(String todoUserName);

    TaskQuery todoUserNameLike(String todoUserName);

    TaskQuery action(Date action);

    TaskQuery actionType(String actionType);

    TaskQuery startTime(Date startTime);

    TaskQuery endTime(Date endTime);

    TaskQuery endTimeIsNull();

    TaskQuery alarmTime(Date alarmTime);

    TaskQuery alarmTimeBefore(Date alarmTime);

    TaskQuery alarmTimeAfter(Date alarmTime);

    TaskQuery dueTime(Date dueTime);

    TaskQuery dueTimeBefore(Date dueTime);

    TaskQuery dueTimeAfter(Date dueTime);

    TaskQuery timingState(Integer timingState);

    TaskQuery alarmState(Integer alarmState);

    TaskQuery overDueState(Integer overDueState);

    TaskQuery suspensionState(Integer suspensionState);

    /**
     * 预留字段
     **/
    // 255字符长度
    TaskQuery reservedText1(String reservedText1);

    TaskQuery reservedText1Like(String reservedText1);

    // 255字符长度
    TaskQuery reservedText2(String reservedText2);

    TaskQuery reservedText2Like(String reservedText2);

    // 255字符长度
    TaskQuery reservedText3(String reservedText3);

    TaskQuery reservedText3Like(String reservedText3);

    // 255字符长度
    TaskQuery reservedText4(String reservedText4);

    TaskQuery reservedText4Like(String reservedText4);

    // 255字符长度
    TaskQuery reservedText5(String reservedText5);

    TaskQuery reservedText5Like(String reservedText5);

    // 255字符长度
    TaskQuery reservedText6(String reservedText6);

    TaskQuery reservedText6Like(String reservedText6);

    // 255字符长度
    TaskQuery reservedText7(String reservedText7);

    TaskQuery reservedText7Like(String reservedText7);

    // 255字符长度
    TaskQuery reservedText8(String reservedText8);

    TaskQuery reservedText8Like(String reservedText8);

    // 255字符长度
    TaskQuery reservedText9(String reservedText9);

    TaskQuery reservedText9Like(String reservedText9);

    // 255字符长度
    TaskQuery reservedText10(String reservedText10);

    TaskQuery reservedText10Like(String reservedText10);

    // 255字符长度
    TaskQuery reservedText11(String reservedText11);

    TaskQuery reservedText11Like(String reservedText11);

    // 255字符长度
    TaskQuery reservedText12(String reservedText12);

    TaskQuery reservedText12Like(String reservedText12);

    TaskQuery reservedNumber1(Integer reservedNumber1);

    TaskQuery reservedNumber2(Double reservedNumber2);

    TaskQuery reservedNumber3(Double reservedNumber3);

    TaskQuery reservedDate1(Date reservedDate1);

    TaskQuery reservedDate2(Date reservedDate2);

    TaskQuery permission(String userId, Collection<Permission> permissions);

    TaskQuery permission(List<String> userSids, Collection<Permission> permissions);

    TaskQuery orderByStartTimeAsc();

    TaskQuery orderByStartTimeDesc();

    TaskQuery orderByAlarmStateAsc();

    TaskQuery orderByAlarmStateDesc();

    TaskQuery orderByOverDueStateAsc();

    TaskQuery orderByOverDueStateDesc();

    // 设置模糊查询连接方式(or,and 默认or)
    TaskQuery setLikeQueryConnector(String likeQueryConnector);

    TaskQuery addReservedText8(String reservedText8);

    TaskQuery addReservedText10(String reservedText10);

    TaskQuery where(String whereSql, Map<String, Object> params);

    /**
     * 使用该方法不能再调用其他orderByXXX方法
     *
     * @param orderBy
     * @return
     */
    TaskQuery order(String orderBy);

    /**
     * 关联查询语句
     *
     * @param join
     */
    TaskQuery join(String join);

    /**
     * 查询字段语句
     *
     * @param projection
     */
    TaskQuery projection(String projection);

}
