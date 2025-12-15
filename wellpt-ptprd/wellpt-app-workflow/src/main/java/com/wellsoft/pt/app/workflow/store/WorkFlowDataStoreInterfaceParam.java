/*
 * @(#)5/15/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.wellsoft.pt.basicdata.datastore.support.DataStoreInterfaceField;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreInterfaceFieldElement;
import com.wellsoft.pt.jpa.criteria.InterfaceParam;
import com.wellsoft.pt.workflow.store.WorkFlowLeftJoinConfig;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 5/15/24.1	zhulh		5/15/24		Create
 * </pre>
 * @date 5/15/24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkFlowDataStoreInterfaceParam implements InterfaceParam {

    @DataStoreInterfaceField(name = "流程", domType = DataStoreInterfaceFieldElement.MULTI_SELECT, service = "flowDefinitionService.loadSelectDataWorkflowDefinition")
    private List<String> flowDefIds;

    @DataStoreInterfaceField(name = "左连接数据模型", domType = DataStoreInterfaceFieldElement.CUSTOM, defaultValue = "wf-datastore-left-join-config")
    private WorkFlowLeftJoinConfig leftJoinConfig;

    /**
     * @return the flowDefIds
     */
    public List<String> getFlowDefIds() {
        return flowDefIds;
    }

    /**
     * @param flowDefIds 要设置的flowDefIds
     */
    public void setFlowDefIds(List<String> flowDefIds) {
        this.flowDefIds = flowDefIds;
    }

    /**
     * @return the leftJoinConfig
     */
    public WorkFlowLeftJoinConfig getLeftJoinConfig() {
        return leftJoinConfig;
    }

    /**
     * @param leftJoinConfig 要设置的leftJoinConfig
     */
    public void setLeftJoinConfig(WorkFlowLeftJoinConfig leftJoinConfig) {
        this.leftJoinConfig = leftJoinConfig;
    }

}
