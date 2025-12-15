/*
 * @(#)4/28/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.support.provider;

import com.wellsoft.pt.app.dingtalk.entity.DingtalkWorkRecordEntity;
import com.wellsoft.pt.app.dingtalk.service.DingtalkWorkRecordService;
import com.wellsoft.pt.bpm.engine.access.FlowAccessPermissionProvider;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/28/25.1	    zhulh		4/28/25		    Create
 * </pre>
 * @date 4/28/25
 */
@Component
public class DingtalkFlowAccessPermissionProviderImpl implements FlowAccessPermissionProvider {

    @Autowired
    private DingtalkWorkRecordService dingtalkWorkRecordService;

    @Override
    public String getName() {
        return "钉钉流程访问权限提供者";
    }

    @Override
    public List<Permission> provide(TaskInstance taskInstance, FlowDefinition flowDefinition) {
        List<DingtalkWorkRecordEntity> entities = dingtalkWorkRecordService.listGroupChatByFlowInstUuidAndTypeAndState(taskInstance.getFlowInstance().getUuid(), DingtalkWorkRecordEntity.Type.USER, DingtalkWorkRecordEntity.State.Sent);
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        boolean hasPermission = entities.stream().filter(entity -> StringUtils.contains(entity.getOaUserId(), currentUserId)).findFirst().isPresent();
        return hasPermission ? Collections.singletonList(AclPermission.READ) : Collections.emptyList();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
