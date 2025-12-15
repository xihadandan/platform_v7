/*
 * @(#)2012-10-19 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;

import java.util.List;
import java.util.Map;

/**
 * Description: 流程定义升级
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年3月1日.1	zhulh		2021年3月1日		Create
 * </pre>
 * @date 2021年3月1日
 */
public interface FlowDefinitionUpgradeService extends BaseService {

    /**
     * 流程定义升级到平台6.2.3版本
     */
    List<String> upgrade2v6_2_3(String flowDefUuid);

    Map<String, String> getUpgradeFlowDefinitionXml(String flowDefUuid);

    FlowDefinition saveUpgradeFlowDefinitionXml(String flowDefUuid, String flowDefXml);

    /**
     * 流程定义升级到平台6.2.5版本
     */
    List<String> upgrade2v6_2_5(String flowDefUuid);

    Map<String, String> getUpgradeFlowDefinitionXml2v6_2_5(String flowDefUuid);

    /**
     * 流程定义升级到平台6.2.7版本——计时器配置升级
     */
    List<String> upgrade2v6_2_7(String flowDefUuid);

    /**
     * @param flowDefUuid
     * @return
     */
    List<String> upgrade2v6_2_9_1(String flowDefUuid);

    /**
     * @param flowDefUuid
     * @return
     */
    List<String> upgrade2v6_2_12(String flowDefUuid);
}
