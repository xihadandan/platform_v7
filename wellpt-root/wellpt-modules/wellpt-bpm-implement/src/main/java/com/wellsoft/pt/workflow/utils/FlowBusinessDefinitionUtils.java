/*
 * @(#)11/21/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.utils;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.workflow.entity.WfFlowBusinessDefinitionEntity;
import com.wellsoft.pt.workflow.service.WfFlowBusinessDefinitionService;
import com.wellsoft.pt.workflow.support.FlowBusinessDefinitionJson;
import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 11/21/22.1	zhulh		11/21/22		Create
 * </pre>
 * @date 11/21/22
 */
public class FlowBusinessDefinitionUtils {

    /**
     * 根据流程业务定义ID获取定义JSON信息
     *
     * @param flowBizDefId
     * @return
     */
    public static FlowBusinessDefinitionJson getBusinessDefinitionJsonById(String flowBizDefId) {
        if (StringUtils.isBlank(flowBizDefId)) {
            return new FlowBusinessDefinitionJson();
        }

        WfFlowBusinessDefinitionService bizProcessDefinitionService = ApplicationContextHolder.getBean(WfFlowBusinessDefinitionService.class);
        WfFlowBusinessDefinitionEntity entity = bizProcessDefinitionService.getById(flowBizDefId);
        if (entity == null) {
            return new FlowBusinessDefinitionJson();
        }

        FlowBusinessDefinitionJson flowBusinessDefinitionJson = null;
        String definitonJson = entity.getDefinitionJson();
        if (StringUtils.isNotBlank(definitonJson)) {
            flowBusinessDefinitionJson = JsonUtils.json2Object(definitonJson, FlowBusinessDefinitionJson.class);
        } else {
            flowBusinessDefinitionJson = new FlowBusinessDefinitionJson();
            BeanUtils.copyProperties(entity, flowBusinessDefinitionJson);
        }
        return flowBusinessDefinitionJson;
    }

}
