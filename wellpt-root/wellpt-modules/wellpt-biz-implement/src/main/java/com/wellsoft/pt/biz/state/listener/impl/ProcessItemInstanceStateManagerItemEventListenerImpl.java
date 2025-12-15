/*
 * @(#)12/7/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state.listener.impl;

import com.wellsoft.pt.biz.enums.EnumStateTriggerType;
import com.wellsoft.pt.biz.listener.event.ProcessItemEvent;
import com.wellsoft.pt.biz.service.BizProcessItemInstanceService;
import com.wellsoft.pt.biz.state.AbstractStateManagerProcessItemListener;
import com.wellsoft.pt.biz.state.support.StateManagerHelper;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import com.wellsoft.pt.biz.support.StateDefinition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description: 业务事项办件状态管理——业务事项事件发生监听
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
public class ProcessItemInstanceStateManagerItemEventListenerImpl extends AbstractStateManagerProcessItemListener {

    @Autowired
    private BizProcessItemInstanceService processItemInstanceService;

    /**
     * 监听器名称
     *
     * @return
     */
    @Override
    public String getName() {
        return "业务事项办件状态管理事项监听器实现";
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
        return -30;
    }

    /**
     * 变更业务状态
     *
     * @param event
     * @param parser
     */
    @Override
    protected void changeBusinessStateIfRequired(ProcessItemEvent event, ProcessDefinitionJsonParser parser) {
        // 业务事项办件状态管理——业务事项事件发生
        Map<String, List<StateDefinition>> itemStateMap = parser.getItemStateMap();
        for (Map.Entry<String, List<StateDefinition>> entry : itemStateMap.entrySet()) {
            List<StateDefinition.StateConfig> stateConfigs = StateManagerHelper.getStateConfigs(entry.getValue(), EnumStateTriggerType.ITEM_EVENT_PUBLISHED, config ->
                    StringUtils.equals(event.getItemCode(), config.getItemCode()) &&
                            StringUtils.equals(event.getEventType(), config.getItemEventId())
            );

            if (CollectionUtils.isEmpty(stateConfigs)) {
                continue;
            }

            ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(entry.getKey());
            // 变更业务事项办件状态
            stateConfigs.forEach(config -> processItemInstanceService.changeItemInstanceState(event, config, processItemConfig.getFormConfig(), processItemConfig, parser));
        }
    }

}
