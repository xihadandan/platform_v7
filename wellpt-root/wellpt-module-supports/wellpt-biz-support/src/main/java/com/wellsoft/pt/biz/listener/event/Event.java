/*
 * @(#)10/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.listener.event;

import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/25/22.1	zhulh		10/25/22		Create
 * </pre>
 * @date 10/25/22
 */
public interface Event {
    /**
     * 获取事件名称
     *
     * @return
     */
    String getName();

    /**
     * 获取事件类型
     *
     * @return
     */
    String getEventType();

    /**
     * 获取业务流程定义UUID
     *
     * @return
     */
    String getProcessDefUuid();

    /**
     * 获取业务流程定义ID
     *
     * @return
     */
    String getProcessDefId();

    /**
     * 获取业务流程实例UUID
     *
     * @return
     */
    String getProcessInstUuid();

    /**
     * 获取表单定义UUID
     *
     * @return
     */
    String getFormUuid();

    /**
     * 获取表单数据UUID
     *
     * @return
     */
    String getDataUuid();

    /**
     * 获取表单数据
     *
     * @return
     */
    DyFormData getDyFormData();

    /**
     * 获取业务主体ID
     *
     * @return
     */
    String getEntityId();

    /**
     * 获取业务流程定义解析
     *
     * @return
     */
    ProcessDefinitionJsonParser getProcessDefinitionJsonParser();

    /**
     * @return
     */
    Map<String, Object> getExtraData();
}
