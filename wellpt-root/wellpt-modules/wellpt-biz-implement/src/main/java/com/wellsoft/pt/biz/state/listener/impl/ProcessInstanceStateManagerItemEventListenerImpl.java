/*
 * @(#)12/7/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state.listener.impl;

import com.wellsoft.pt.biz.enums.EnumStateTriggerType;
import com.wellsoft.pt.biz.listener.event.ProcessItemEvent;
import com.wellsoft.pt.biz.service.BizProcessInstanceService;
import com.wellsoft.pt.biz.state.AbstractStateManagerProcessItemListener;
import com.wellsoft.pt.biz.state.support.StateManagerHelper;
import com.wellsoft.pt.biz.support.ProcessDefinitionJson;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.StateDefinition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 业务办件状态管理——业务事项事件发生监听
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/7/23.1	zhulh		12/7/23		Create
 * </pre>
 * @date 12/7/23
 */
@Component
public class ProcessInstanceStateManagerItemEventListenerImpl extends AbstractStateManagerProcessItemListener {

    @Autowired
    private BizProcessInstanceService processInstanceService;

    /**
     * 监听器名称
     *
     * @return
     */
    @Override
    public String getName() {
        return "业务办件状态管理事项监听器实现";
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return -10;
    }

    @Override
    protected void changeBusinessStateIfRequired(ProcessItemEvent event, ProcessDefinitionJsonParser parser) {
        // 业务办件状态管理——业务事项事件发生
        ProcessDefinitionJson.ProcessFormConfig processFormConfig = parser.getProcessFormConfig();
        if (processFormConfig == null) {
            return;
        }
        List<StateDefinition.StateConfig> stateConfigs = StateManagerHelper.
                getStateConfigs(processFormConfig.getStates(), EnumStateTriggerType.ITEM_EVENT_PUBLISHED, config ->
                        StringUtils.equals(event.getItemCode(), config.getItemCode()) &&
                                StringUtils.equals(event.getEventType(), config.getItemEventId())
                );

        // 变更业务办件状态
        if (CollectionUtils.isNotEmpty(stateConfigs)) {
            stateConfigs.forEach(config -> processInstanceService.changeProcessInstanceState(event, config, processFormConfig, parser));
        }
    }

}
