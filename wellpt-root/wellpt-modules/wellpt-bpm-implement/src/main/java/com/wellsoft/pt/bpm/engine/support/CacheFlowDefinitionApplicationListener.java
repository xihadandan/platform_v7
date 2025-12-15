/*
 * @(#)2021年12月16日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.wellsoft.context.profile.OnNotDevProfileCondition;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.workflow.service.FlowSchemeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 应用启动后缓存最新更改的流程定义
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年12月16日.1	zhulh		2021年12月16日		Create
 * </pre>
 * @date 2021年12月16日
 */
@Component
@Conditional(OnNotDevProfileCondition.class)
public class CacheFlowDefinitionApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private FlowDefinitionService flowDefinitionService;

    @Autowired
    private FlowSchemeService flowSchemeService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 应用启动后缓存最新更改的50个流程定义
        try {
            List<String> flowDefUuids = flowDefinitionService.getLatestUpdatedUuids(50);
            for (String flowDefUuid : flowDefUuids) {
                flowSchemeService.getFlowElementByFlowDefUuid(flowDefUuid);
            }
        } catch (Exception e) {
            logger.error("启动更新流程定义缓存失败：", e);
        }

    }

}
