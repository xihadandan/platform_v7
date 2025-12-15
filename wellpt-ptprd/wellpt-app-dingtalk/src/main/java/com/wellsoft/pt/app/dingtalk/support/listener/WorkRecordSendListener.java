/*
 * @(#)2020年5月27日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.support.listener;

import com.dingtalk.api.request.OapiWorkrecordAddRequest.FormItemVo;
import com.google.common.collect.Lists;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.app.dingtalk.entity.MultiOrgDingUser;
import com.wellsoft.pt.app.dingtalk.entity.WorkRecord;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgDingUserService;
import com.wellsoft.pt.app.dingtalk.service.WorkRecordService;
import com.wellsoft.pt.app.dingtalk.support.DingtalkConfig;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkApiUtils;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkUtils;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.jpa.event.WellptTransactionalEventListener;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.event.WorkTodoEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月27日.1	zhongzh		2020年5月27日		Create
 * </pre>
 * @date 2020年5月27日
 */
//@Component
@Deprecated
public class WorkRecordSendListener extends WellptTransactionalEventListener<WorkTodoEvent> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TaskService taskService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private DingtalkConfig dingtalkConfig;

    @Autowired
    private WorkRecordService workRecordService;

    @Autowired
    private MultiOrgUserService multiOrgUserService;

    @Autowired
    private MultiOrgDingUserService multiOrgDingUserService;

    /**
     *
     */
    @Override
    public void onApplicationEvent(WorkTodoEvent event) {
        String flowInstUuid = null, taskInstUuid = null;
        FlowInstance flowInstance = event.getFlowInstance();
        TaskInstance taskInstance = event.getTaskInstance();
        if ((flowInstUuid = flowInstance.getUuid()) == null || (taskInstUuid = taskInstance.getUuid()) == null) {
            return;// 流程数据未持久化（比如办理人异常）
        }
        Set<String> userIds = event.getUserIds();
        String authUri, title = flowInstance.getTitle();
        // 构建跳转url
        authUri = dingtalkConfig.getCorpDomainUri() + "/mobile/pt/dingtalk/start?uri=%s";
        String redirectUri = DingtalkUtils.urlEncode(DingtalkUtils.uriFormat(
                "/workflow/mobile/work/view/todo2?flowInstUuid=%s&taskInstUuid=%s&title=%s", flowInstUuid,
                taskInstUuid, title));
        // 构建formItemList
        List<FormItemVo> formItemList = Lists.newArrayList();
        FormItemVo itemOperator = new FormItemVo();
        itemOperator.setTitle("前办理人");
        String currentUserName = SpringSecurityUtils.getCurrentUserName();
        itemOperator.setContent(currentUserName);
        formItemList.add(itemOperator);
        FormItemVo itemTaskName = new FormItemVo();
        itemTaskName.setTitle("办理环节");
        itemTaskName.setContent(taskInstance.getName());
        formItemList.add(itemTaskName);
        FormItemVo itemCreateTime = new FormItemVo();
        itemCreateTime.setTitle("开始时间");
        itemCreateTime.setContent(DateUtils.formatDateTimeMin(taskInstance.getStartTime()));
        formItemList.add(itemCreateTime);
        for (String userId : userIds) {
            MultiOrgDingUser multiOrgDingUser = multiOrgDingUserService.getByPtUserId(userId);
            if (multiOrgDingUser == null) {
                logger.warn("flowInstUuid[" + flowInstUuid + "]taskInstUuid[" + taskInstUuid + "]userId[" + userId
                        + "]未找到钉钉用户");
                continue;
            }
            // errorcode 40035:标题小于50字符
            title = StringUtils.abbreviate(title, 49);
            String dingUserId = multiOrgDingUser.getDing_userId();
            MultiOrgUserAccount account = multiOrgUserService.getAccountByUserId(userId);
            String userName = account == null ? null : account.getUserName();
            String pushMode = dingtalkConfig.getPushMode();
            if (WorkRecord.PUSH_MODE_SYNC.equals(pushMode)) {
                DingtalkApiUtils.addWorkRecord(taskInstUuid, dingUserId, userName, DingtalkApiUtils.getAccessToken(),
                        title, DingtalkUtils.uriFormat(authUri, redirectUri), formItemList);
            } else if (WorkRecord.PUSH_MODE_ASYNC.equals(pushMode)) {
                DingtalkApiUtils.addWorkRecord2(taskInstUuid, dingUserId, userName, DingtalkApiUtils.getAccessToken(),
                        title, DingtalkUtils.uriFormat(authUri, redirectUri), formItemList);
            }
        }
    }

}
