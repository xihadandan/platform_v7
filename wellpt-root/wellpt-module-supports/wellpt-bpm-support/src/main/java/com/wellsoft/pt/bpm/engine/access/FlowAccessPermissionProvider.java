/*
 * @(#)2021年7月27日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import org.springframework.core.Ordered;
import org.springframework.security.acls.model.Permission;

import java.util.List;

/**
 * Description: 流程访问权限提供者
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
public interface FlowAccessPermissionProvider extends Ordered {

    /**
     * 访问权限提供者名称
     *
     * @return
     */
    String getName();

    /**
     * 提供可访问流程环节实例的权限列表
     *
     * @param taskInstance
     * @return
     */
    List<Permission> provide(TaskInstance taskInstance, FlowDefinition flowDefinition);

}
