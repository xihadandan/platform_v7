/*
 * @(#)2021年7月27日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access.impl;

import com.wellsoft.pt.bpm.engine.access.FlowAccessPermissionProvider;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Description: 空的流程访问权限提供者
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年7月27日.1	zhulh		2021年7月27日		Create
 * </pre>
 * @date 2021年7月27日
 */
@Component
public class EmptyFlowAccessPermissionProvider implements FlowAccessPermissionProvider {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.access.FlowAccessPermissionProvider#getName()
     */
    @Override
    public String getName() {
        return "空的流程访问权限提供者";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.access.FlowAccessPermissionProvider#provide(com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowDefinition)
     */
    @Override
    public List<Permission> provide(TaskInstance taskInstance, FlowDefinition flowDefinition) {
        return Collections.emptyList();
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 0;
    }

}
