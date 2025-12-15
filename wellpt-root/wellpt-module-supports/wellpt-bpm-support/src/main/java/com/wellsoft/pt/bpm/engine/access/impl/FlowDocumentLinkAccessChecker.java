/*
 * @(#)Mar 15, 2022 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access.impl;

import com.wellsoft.pt.basicdata.documentlink.support.AbstractDocumentLinkAccessChecker;
import com.wellsoft.pt.basicdata.documentlink.support.DocumentLinkInfo;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 流程实例文档链接关系，流程权限检查
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Mar 15, 2022.1	zhulh		Mar 15, 2022		Create
 * </pre>
 * @date Mar 15, 2022
 */
@Component
public class FlowDocumentLinkAccessChecker extends AbstractDocumentLinkAccessChecker {

    @Autowired
    private FlowService flowService;

    @Autowired
    private TaskService taskService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.documentlink.support.AbstractDocumentLinkAccessChecker#getName()
     */
    @Override
    public String getName() {
        return "flowDocumentLinkAccessChecker";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.documentlink.support.AbstractDocumentLinkAccessChecker#check(java.lang.String, com.wellsoft.pt.basicdata.documentlink.support.DocumentLinkInfo)
     */
    @Override
    public boolean check(String flowInstUuid, DocumentLinkInfo documentLinkInfo) {
        // 源数据是草稿
        if (StringUtils.equals(flowInstUuid, documentLinkInfo.getSourceDataUuid())) {
            if (flowService.hasDraftPermission(SpringSecurityUtils.getCurrentUserId(), flowInstUuid)) {
                return true;
            }
        }

        String taskInstUuid = taskService.getLastTaskInstanceUuidByFlowInstUuid(flowInstUuid);
        if (StringUtils.isBlank(taskInstUuid)) {
            return false;
        }
        return taskService.hasViewPermissionCurrentUser(taskInstUuid);
    }

}
